package gg.aquatic.aquaticcrates.plugin.crate

import com.nexomc.nexo.utils.flatMapFast
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationManager
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationSettings
import gg.aquatic.aquaticcrates.api.event.CrateOpenEvent
import gg.aquatic.aquaticcrates.api.player.HistoryHandler
import gg.aquatic.aquaticcrates.api.player.crateEntry
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.aquaticcrates.plugin.animation.open.settings.InstantAnimationSettings
import gg.aquatic.aquaticcrates.plugin.reward.RewardManagerImpl
import gg.aquatic.aquaticcrates.plugin.reward.RolledRewardImpl
import gg.aquatic.waves.profile.toAquaticPlayer
import gg.aquatic.waves.util.audience.AquaticAudience
import gg.aquatic.waves.util.audience.FilterAudience
import gg.aquatic.waves.util.chance.randomItem
import gg.aquatic.waves.util.collection.executeActions
import gg.aquatic.waves.util.collection.mapPair
import gg.aquatic.waves.util.task.AsyncCtx
import gg.aquatic.waves.util.task.BukkitCtx
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext
import kotlin.math.max
import kotlin.time.measureTime

class BasicOpenManager(val crate: BasicCrate) {

    suspend fun instantOpen(player: Player) = withContext(AsyncCtx) {
        val profile = player.toAquaticPlayer()?.crateEntry() ?: return@withContext

        CrateOpenEvent(crate, player).callEvent()

        val rewards = crate.rewardManager.getRewards(player)
        for (reward in rewards) {
            reward.give(player, false)
        }

        crate.animationManager.animationSettings.finalAnimationTasks.executeActions(
            object : CrateAnimation() {
                override val animationManager: CrateAnimationManager = crate.animationManager
                override val rewards: MutableList<RolledReward> = rewards
                override val completionFuture: CompletableFuture<CrateAnimation> =
                    CompletableFuture.completedFuture(this)
                override val settings: CrateAnimationSettings = crate.animationManager.animationSettings

                override fun onReroll() {

                }

                override val player: Player = player
                override val baseLocation: Location = player.location
                override val audience: AquaticAudience = FilterAudience { it == player }

            }
        ) { a, str -> a.updatePlaceholders(str) }

        val milestones = crate.rewardManager.milestoneManager.milestonesReached(player)
        for (milestone in milestones) {
            for (reward in milestone.rewards) {
                reward.give(player, 1, false)
            }
        }

        if (!crate.disableLogging) {
            HistoryHandler.registerCrateOpen(
                profile,
                crate.identifier,
                rewards.mapPair { it.reward to it.randomAmount })
        }
        InstantAnimationSettings.execute(player, crate.animationManager)
    }

    suspend fun open(player: Player, location: Location) = withContext(AsyncCtx) {
        val profile = player.toAquaticPlayer()?.crateEntry() ?: return@withContext

        val rewards = crate.rewardManager.getRewards(player)
        CrateOpenEvent(crate, player).callEvent()

        crate.animationManager.animationSettings.create(
            player, crate.animationManager, location, rewards
        ).also { animation ->
            val milestones = crate.rewardManager.milestoneManager.milestonesReached(player)
            for (milestone in milestones) {
                for (reward in milestone.rewards) {
                    reward.give(player, 1, false)
                }
            }
            if (!crate.disableLogging) {
                HistoryHandler.registerCrateOpen(
                    profile,
                    crate.identifier,
                    animation.rewards.mapPair { it.reward to it.randomAmount })
            }
        }
    }

    private data class RewardStats(var count: Int, var amount: Int)

    suspend fun massOpen(player: Player, amount: Int) = withContext(AsyncCtx) {
        val threads = idealThreads()
        //val wonRewards = ConcurrentHashMap<Reward, Pair<AtomicInteger, AtomicInteger>>()
        val allRewards = crate.rewardManager.rewards.values

        val hasRandomAmount = if ((crate.rewardManager as RewardManagerImpl).possibleRewardRanges.size == 1) {
            val range = crate.rewardManager.possibleRewardRanges.first()
            !(range.min == 1 && range.max == 1)
        } else crate.rewardManager.possibleRewardRanges.size > 1

        val duration = measureTime {
            val wonRewards: List<HashMap<Reward, RewardStats>> = parallelForEach(
                total = amount,
                parallelism = threads,
                context = AsyncCtx
            ) { range ->
                val skipScaleBase = (5 * (amount.toDouble() / 100_000.0)).toInt()

                val wonRewards = HashMap<Reward, RewardStats>(allRewards.size)
                var skip = 0
                val last = range.size
                for (i in 0 until range.size) {
                    if (skip-- > 0) continue

                    if (hasRandomAmount) {
                        val rewardsAmount =
                            crate.rewardManager.possibleRewardRanges.randomItem()?.randomNum ?: 1
                        val rewards = getRandomRewards(rewardsAmount, allRewards)
                        for (reward in rewards) {
                            val current = wonRewards[reward.reward]
                            if (current == null) {
                                wonRewards[reward.reward] = RewardStats(1, reward.randomAmount)
                                continue
                            }
                            current.count++
                            current.amount += reward.randomAmount
                        }
                    } else {
                        val reward = allRewards.randomItem() ?: continue
                        if (skipScaleBase > 1 && reward.chance > 0.1 && i % skipScaleBase == 0 && i + skipScaleBase < last) {
                            val skipScale = max(2, if (reward.chance > 0.2) skipScaleBase else skipScaleBase / 2)
                            skip = skipScale - 1
                            val current = wonRewards[reward]
                            if (current == null) {
                                wonRewards[reward] = RewardStats(skipScale, skipScale)
                                continue
                            }
                            current.count += skipScale
                            current.amount += skipScale
                            continue
                        }
                        val current = wonRewards[reward]
                        if (current == null) {
                            wonRewards[reward] = RewardStats(1, 1)
                            continue
                        }
                        current.count++
                        current.amount++
                    }
                }
                wonRewards
            }

            var totalWon = 0
            var totalWonExcluded = 0

            val wonRewardsFinal = HashMap<Reward, RewardStats>(allRewards.size)

            for (map in wonRewards) {
                for ((reward, amounts) in map) {
                    val previous = wonRewardsFinal[reward]

                    totalWon += amounts.amount
                    totalWonExcluded += amounts.count

                    if (previous == null) {
                        wonRewardsFinal[reward] = amounts
                    } else {
                        previous.amount += amounts.amount
                        previous.count += amounts.count
                    }
                }
            }

            withContext(BukkitCtx) {
                wonRewardsFinal.forEach { (reward, pair) ->
                    reward.massGive(player, pair.amount, pair.count)
                }
                crate.massOpenFinalActions.executeActions(player) { p, str ->
                    str.replace("%total-won%", totalWon.toString())
                        .replace("%total-won-excluded%", totalWonExcluded.toString())
                        .replace("%player%", p.name)
                }
                wonRewardsFinal.clear()
            }
        }
        player.sendMessage("Took ${duration.inWholeMilliseconds}ms (${duration.inWholeSeconds}s) to open $amount crates")
    }

    private fun getRandomRewards(
        amount: Int,
        possibleRewards: Collection<Reward>
    ): List<RolledReward> {
        val finalRewards = HashMap<String, Pair<Reward, Int>>()
        var amountLeft = amount
        if (possibleRewards.isEmpty()) return listOf()

        val rolledRewards = ArrayList<RolledReward>()
        while (amountLeft > 0) {
            val randomReward = possibleRewards.randomItem() ?: return listOf()
            val previous = finalRewards[randomReward.id]
            finalRewards[randomReward.id] = (previous?.first ?: randomReward) to ((previous?.second ?: 0) + 1)
            amountLeft--
        }

        for ((_, pair) in finalRewards) {
            val reward = pair.first
            val amount = pair.second
            for (i in 0..<amount) {
                rolledRewards += RolledRewardImpl(reward, reward.amountRanges.randomItem()?.randomNum ?: 1)
            }
        }
        return rolledRewards
    }

    private fun idealThreads(): Int {
        val cores = Runtime.getRuntime().availableProcessors()
        val maxThreads = cores * 2
        return maxThreads
    }

    private suspend fun <T> parallelForEach(
        total: Int,
        parallelism: Int,
        context: CoroutineContext,
        block: suspend (range: List<Int>) -> T
    ) = coroutineScope {
        val chunkSize = max(1, total / parallelism)

        (0 until total).chunked(chunkSize).map { chunk ->
            async(context) {
                block(chunk)
            }
        }.awaitAll()
    }
}