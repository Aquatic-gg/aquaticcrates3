package gg.aquatic.aquaticcrates.plugin.crate

import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate
import gg.aquatic.aquaticcrates.api.player.crateEntry
import gg.aquatic.aquaticseries.lib.util.mapPair
import gg.aquatic.waves.profile.toAquaticPlayer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

class BasicOpenManager(val crate: BasicCrate) {

    fun open(player: Player, location: org.bukkit.Location, spawnedCrate: SpawnedCrate?): CompletableFuture<Void> {
        val crateEntry = player.toAquaticPlayer()?.crateEntry() ?: return CompletableFuture.completedFuture(null)

        val rewards = crate.rewardManager.getRewards(player)
        crateEntry.registerCrateOpen(crate.identifier,rewards.mapPair { it.reward.id to it.randomAmount })

        Bukkit.broadcastMessage("Opening crate!")
        return crate.animationManager.animationSettings.create(
            player, crate.animationManager, location, rewards
        ).thenRun {
            val milestones = crate.rewardManager.milestoneManager.milestonesReached(player)
            for (milestone in milestones) {
                for (reward in milestone.rewards) {
                    reward.give(player,1)
                }
            }
        }
    }
}