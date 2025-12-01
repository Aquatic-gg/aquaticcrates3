package gg.aquatic.aquaticcrates.plugin.crate

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationManager
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationSettings
import gg.aquatic.aquaticcrates.api.event.CrateOpenEvent
import gg.aquatic.aquaticcrates.api.player.HistoryHandler
import gg.aquatic.aquaticcrates.api.player.crateEntry
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.aquaticcrates.plugin.CratesPlugin
import gg.aquatic.aquaticcrates.plugin.animation.open.settings.InstantAnimationSettings
import gg.aquatic.aquaticcrates.plugin.reward.RewardManagerImpl
import gg.aquatic.aquaticcrates.plugin.reward.RolledRewardImpl
import gg.aquatic.waves.item.option.AmountOptionHandle
import gg.aquatic.waves.item.option.ItemOptions
import gg.aquatic.waves.profile.toAquaticPlayer
import gg.aquatic.waves.util.audience.AquaticAudience
import gg.aquatic.waves.util.audience.FilterAudience
import gg.aquatic.waves.util.chance.IChance
import gg.aquatic.waves.util.chance.randomItem
import gg.aquatic.waves.util.collection.executeActions
import gg.aquatic.waves.util.collection.mapPair
import gg.aquatic.waves.util.task.AsyncCtx
import gg.aquatic.waves.util.task.BukkitCtx
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.Location
import org.bukkit.entity.Player
import java.math.BigInteger
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.ThreadLocalRandom
import kotlin.coroutines.CoroutineContext
import kotlin.math.max
import kotlin.math.min

class BasicOpenManager(val crate: BasicCrate) {

    suspend fun instantOpen(player: Player) = withContext(CacheCtx) {
        val profile = player.toAquaticPlayer()?.crateEntry() ?: return@withContext

        CrateOpenEvent(crate, player).callEvent()

        val rewards = crate.rewardManager.getRewards(player)
        for (reward in rewards) {
            reward.give(player)
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
                reward.give(player, 1)
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

    suspend fun open(player: Player, location: Location) = withContext(CacheCtx) {
        val profile = player.toAquaticPlayer()?.crateEntry() ?: return@withContext

        val rewards = crate.rewardManager.getRewards(player)
        CrateOpenEvent(crate, player).callEvent()

        crate.animationManager.animationSettings.create(
            player, crate.animationManager, location, rewards
        ).also { animation ->
            val milestones = crate.rewardManager.milestoneManager.milestonesReached(player)
            for (milestone in milestones) {
                for (reward in milestone.rewards) {
                    reward.give(player, 1)
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

    private data class RewardStats(var count: Int, var amount: BigInteger)

    private fun <T : IChance> getRandomItem(items: Collection<T>, rnd: ThreadLocalRandom, totalWeight: Double): T? {
        var random = rnd.nextDouble() * totalWeight
        for (item in items) {
            random -= item.chance
            if (random <= 0.0) {
                return item
            }
        }
        return null
    }

    suspend fun massOpen(player: Player, amount: Int) = withContext(CacheCtx) {
        val threads = idealThreads()
        //val wonRewards = ConcurrentHashMap<Reward, Pair<AtomicInteger, AtomicInteger>>()
        val allRewards = crate.rewardManager.rewards.values
        val totalWeight = allRewards.sumOf { it.chance }

        val amountRanges = (crate.rewardManager as RewardManagerImpl).possibleRewardRanges
        val amountTotalWeight = amountRanges.sumOf { it.chance }

        val hasRandomAmount = if (amountRanges.size == 1) {
            val range = amountRanges.first()
            !(range.min == 1 && range.max == 1)
        } else amountRanges.size > 1

        val hasStaticRandomAmount = HashMap<String, Long>()
        for (reward in allRewards) {
            if (reward.amountRanges.isEmpty()) {
                val amount = (reward.item.getOption(ItemOptions.AMOUNT) as? AmountOptionHandle)?.amount ?: 1
                hasStaticRandomAmount[reward.id] = amount.toLong()
            } else if (reward.amountRanges.size == 1) {
                val range = reward.amountRanges.first()
                if (range.min == range.max) {
                    hasStaticRandomAmount[reward.id] = range.min.toLong()
                }
            }
        }

        val wonRewards: List<HashMap<Reward, RewardStats>> = parallelForEach(
            total = amount,
            parallelism = threads,
            context = AsyncCtx
        ) { range ->
            val rnd = ThreadLocalRandom.current()
            val wonRewards = HashMap<Reward, RewardStats>(allRewards.size)

            if (hasRandomAmount) {
                for (i in range) {
                    val rewardsAmount =
                        getRandomItem(amountRanges, rnd, amountTotalWeight)?.randomNum ?: 1
                    val rewards = getRandomRewards(rewardsAmount, allRewards, rnd, amountTotalWeight)
                    for (reward in rewards) {
                        val current = wonRewards[reward.reward]
                        if (current == null) {
                            wonRewards[reward.reward] = RewardStats(1, reward.randomAmount.toBigInteger())
                            continue
                        }
                        current.count++
                        current.amount += reward.randomAmount.toBigInteger()
                    }
                }
            } else {
                val skipScaleBase = (5 * (amount.toDouble() / 100_000.0)).toInt()
                var skip = 0
                val last = range.last

                for (i in range) {
                    if (skip-- > 0) continue

                    val reward = getRandomItem(allRewards, rnd, totalWeight) ?: continue

                    val staticRandomAmount = hasStaticRandomAmount[reward.id]
                    var isRandom = false
                    val amount = staticRandomAmount
                        ?: let {
                            isRandom = true
                            val random = reward.amountRanges.randomItem()?.randomNum?.toLong()
                            if (random != null) {
                                isRandom = true
                                random
                            } else {
                                1L
                            }
                        }

                    if (isRandom) {
                        wonRewards.computeIfPresent(reward) { _, second ->
                            second.apply {
                                count++
                                this.amount += amount.toBigInteger()
                            }
                        } ?: let {
                            wonRewards[reward] = RewardStats(1, amount.toBigInteger())
                        }
                        continue
                    }
                    if (skipScaleBase > 1 && reward.chance > 0.1 && i % skipScaleBase == 0 && i + skipScaleBase < last) {
                        val skipScale = max(2, if (reward.chance > 0.2) skipScaleBase else skipScaleBase / 2)
                        skip = skipScale - 1
                        val current = wonRewards[reward]
                        if (current == null) {
                            wonRewards[reward] = RewardStats(skipScale, skipScale.toBigInteger() * amount.toBigInteger())
                            continue
                        }
                        current.count += skipScale
                        current.amount += skipScale.toBigInteger() * amount.toBigInteger()
                        continue
                    }
                    val current = wonRewards[reward]
                    if (current == null) {
                        wonRewards[reward] = RewardStats(1, amount.toBigInteger())
                        continue
                    }
                    current.count++
                    current.amount += amount.toBigInteger()
                }
            }
            wonRewards
        }

        var totalWon = BigInteger.ZERO
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

        BukkitCtx {
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

    private fun getRandomRewards(
        amount: Int,
        possibleRewards: Collection<Reward>,
        rnd: ThreadLocalRandom,
        totalWeight: Double
    ): List<RolledReward> {
        var amountLeft = amount
        if (possibleRewards.isEmpty()) return listOf()

        val rolledRewards = ArrayList<RolledReward>()
        while (amountLeft > 0) {
            val randomReward = getRandomItem(possibleRewards, rnd, totalWeight) ?: return listOf()
            rolledRewards += RolledRewardImpl(randomReward, randomReward.amountRanges.randomItem()?.randomNum ?: 1)
            amountLeft--
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
        block: suspend (range: IntRange) -> T
    ) = coroutineScope {
        val chunkSize = max(1, total / parallelism)

        (0 until parallelism).map { i ->
            val start = i * chunkSize
            val end = min(total, start + chunkSize)
            if (start >= end) return@map null

            async(context) {
                block(start until end)
            }
        }.filterNotNull().awaitAll()
    }

    object CacheCtx : CoroutineDispatcher() {

        // Single worker thread dedicated to cache operations
        private val executor = Executors.newSingleThreadExecutor(
            Thread.ofPlatform()
                .name("Cache-Worker", 0)
                .daemon(true)
                .uncaughtExceptionHandler { t, e ->
                    CratesPlugin.getInstance().logger.severe("Unhandled exception on $t in CacheCtx")
                    e.printStackTrace()
                }
                .factory()
        )

        val scope = CoroutineScope(
            this + SupervisorJob() + CoroutineExceptionHandler { _, e ->
                CratesPlugin.getInstance().logger.severe("Coroutine exception in CacheCtx")
                e.printStackTrace()
            }
        )

        override fun isDispatchNeeded(context: CoroutineContext): Boolean = true

        override fun dispatch(context: CoroutineContext, block: Runnable) {
            executor.execute(block)
        }

        // Public helpers

        fun launch(block: suspend CoroutineScope.() -> Unit) = scope.launch(block = block)

        fun post(task: () -> Unit) {
            executor.execute(task)
        }

        fun shutdown() {
            executor.shutdown()
        }
    }
}