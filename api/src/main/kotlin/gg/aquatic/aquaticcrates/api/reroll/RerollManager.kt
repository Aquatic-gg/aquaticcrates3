package gg.aquatic.aquaticcrates.api.reroll

import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

abstract class RerollManager(
) {

    abstract val crate: Crate
    abstract val groups: HashMap<String,Int>
    abstract val rerollInput: RerollInput
    abstract val animationTasks: Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>

    abstract fun openReroll(player: Player, animation: CrateAnimation, rewards: Collection<RolledReward>): CompletableFuture<RerollResult>

    class RerollResult(
        val reroll: Boolean
    )

    fun availableRerolls(player: Player): Int {
        var availableRerolls = 0
        for ((id, rerolls) in groups) {
            if (!player.hasPermission("aquaticcrates.reroll.$id")) continue
            if (rerolls > availableRerolls) {
                availableRerolls = rerolls
            }
        }
        return availableRerolls
    }
}