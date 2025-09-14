package gg.aquatic.aquaticcrates.plugin.crate

import com.nexomc.nexo.utils.EventUtils.call
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationManager
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationSettings
import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate
import gg.aquatic.aquaticcrates.api.event.CrateOpenEvent
import gg.aquatic.aquaticcrates.api.player.HistoryHandler
import gg.aquatic.aquaticcrates.api.player.crateEntry
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.aquaticcrates.plugin.animation.open.settings.InstantAnimationSettings
import gg.aquatic.waves.profile.toAquaticPlayer
import gg.aquatic.waves.util.audience.AquaticAudience
import gg.aquatic.waves.util.audience.FilterAudience
import gg.aquatic.waves.util.collection.executeActions
import gg.aquatic.waves.util.collection.mapPair
import gg.aquatic.waves.util.runAsync
import gg.aquatic.waves.util.runSync
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class BasicOpenManager(val crate: BasicCrate) {

    companion object {
        private const val THREADS_LIMIT = 2
    }

    fun instantOpen(player: Player, massOpen: Boolean = false) {
        player.toAquaticPlayer()?.crateEntry() ?: return

        val runnable = {
            CrateOpenEvent(crate, player).callEvent()
        }
        if (Bukkit.isPrimaryThread()) {
            runAsync {
                runnable()
            }
        } else {
            runnable()
        }

        val rewards = crate.rewardManager.getRewards(player)
        for (reward in rewards) {
            reward.give(player, massOpen)
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

        HistoryHandler.registerCrateOpen(player, crate.identifier, rewards.mapPair { it.reward to it.randomAmount })
        InstantAnimationSettings.execute(player, crate.animationManager)
    }

    fun open(player: Player, location: Location, spawnedCrate: SpawnedCrate?): CompletableFuture<Void> {
        player.toAquaticPlayer()?.crateEntry() ?: return CompletableFuture.completedFuture(null)

        val rewards = crate.rewardManager.getRewards(player)
        val runnable = {
            CrateOpenEvent(crate, player).callEvent()
        }
        if (Bukkit.isPrimaryThread()) {
            runAsync {
                runnable()
            }
        } else {
            runnable()
        }

        return crate.animationManager.animationSettings.create(
            player, crate.animationManager, location, rewards
        ).thenAccept { animation ->
            val milestones = crate.rewardManager.milestoneManager.milestonesReached(player)
            for (milestone in milestones) {
                for (reward in milestone.rewards) {
                    reward.give(player, 1, false)
                }
            }

            HistoryHandler.registerCrateOpen(
                player,
                crate.identifier,
                animation.rewards.mapPair { it.reward to it.randomAmount })
        }
    }

    fun massOpen(player: Player, amount: Int, threadsAmount: Int?): CompletableFuture<Void> {
        val threads = threadsAmount ?: THREADS_LIMIT
        //val crateEntry = player.toAquaticPlayer()?.crateEntry() ?: return CompletableFuture.completedFuture(null)
        //player.sendMessage("Mass opening!")
        val finalFuture = CompletableFuture<Void>()

        val runnable = {
            CrateOpenEvent(crate, player, amount).callEvent()
        }
        if (Bukkit.isPrimaryThread()) {
            runAsync {
                runnable()
            }
        } else {
            runnable()
        }

        if (amount > 10000) {
            //val previousTime = System.currentTimeMillis()
            val wonRewards = ConcurrentHashMap<Reward, Pair<AtomicInteger, AtomicInteger>>()
            runAsync {

                val separated = (amount / threads)
                val lastAmount = amount - (separated * (threads - 1))

                val tasksToJoin = mutableSetOf<CompletableFuture<Void>>()
                for (i in 0 until threads) {
                    val amt = if (i == threads - 1) lastAmount else separated
                    tasksToJoin += CompletableFuture.runAsync {
                        for (ignored in 0 until amt) {
                            val rewards = crate.rewardManager.getRewards(player)
                            HistoryHandler.registerCrateOpen(
                                player,
                                crate.identifier,
                                rewards.mapPair { it.reward to it.randomAmount })
                            for (reward in rewards) {
                                val current =
                                    wonRewards.getOrPut(reward.reward) { (AtomicInteger(0) to AtomicInteger(0)) }
                                current.first.getAndAdd(1)
                                current.second.getAndAdd(reward.randomAmount)
                                reward.give(player, true)
                            }
                        }
                    }
                }
                for (completableFuture in tasksToJoin) {
                    completableFuture.join()
                }
                val totalWon = wonRewards.values.sumOf { it.second.get() }
                val totalWonExcluded = wonRewards.values.sumOf { it.first.get() }
                runSync {
                    crate.massOpenFinalActions.executeActions(player) { p, str ->
                        str.replace("%total-won%", totalWon.toString().replace("%player%", p.name))
                            .replace("%total-won-excluded%", totalWonExcluded.toString())
                    }
                }

                for ((reward, amtPair) in wonRewards) {
                    val amt = amtPair.first.get()
                    val amtTotal = amtPair.second.get()
                    runSync {
                        reward.massGive(player, amtTotal, amt)
                        crate.massOpenPerRewardActions.executeActions(player) { p, str ->
                            str.replace("%reward%", reward.displayName)
                                .replace("%amount%", amt.toString())
                                .replace("%amount-total%", amtTotal.toString())
                                .replace("%player%", p.name)
                        }
                    }

                }
                System.gc()
                //player.sendMessage("Completed in ${System.currentTimeMillis() - previousTime}ms")
                finalFuture.complete(null)
            }

            return finalFuture
        }

        runAsync {
            val wonRewards = ConcurrentHashMap<Reward, Pair<Int, Int>>()
            for (i in 0 until amount) {
                val rewards = crate.rewardManager.getRewards(player)
                HistoryHandler.registerCrateOpen(
                    player,
                    crate.identifier,
                    rewards.mapPair { it.reward to it.randomAmount })
                for (reward in rewards) {
                    val current = wonRewards[reward.reward] ?: (0 to 0)
                    wonRewards[reward.reward] = current.first + 1 to current.second + reward.randomAmount
                    reward.give(player, true)
                }
            }
            val totalWon = wonRewards.values.sumOf { it.second }
            val totalWonExcluded = wonRewards.values.sumOf { it.first }

            runSync {
                crate.massOpenFinalActions.executeActions(player) { p, str ->
                    str.replace("%total-won%", totalWon.toString().replace("%player%", p.name))
                        .replace("%total-won-excluded%", totalWonExcluded.toString())
                }
            }

            for ((reward, amtPair) in wonRewards) {
                val amt = amtPair.first
                val amtTotal = amtPair.second
                runSync {
                    crate.massOpenPerRewardActions.executeActions(player) { p, str ->
                        str.replace("%reward%", reward.displayName)
                            .replace("%amount%", amt.toString())
                            .replace("%amount-total%", amtTotal.toString())
                            .replace("%player%", p.name)
                    }
                }
            }

            finalFuture.complete(null)
        }
        return finalFuture
    }
}