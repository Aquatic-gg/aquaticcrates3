package gg.aquatic.aquaticcrates.api.reroll

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

abstract class RerollManager(
) {

    abstract val crate: Crate
    abstract val groups: HashMap<String,Int>
    abstract val rerollInput: RerollInput

    abstract fun openReroll(player: Player, animation: CrateAnimation, rewards: Collection<RolledReward>): CompletableFuture<RerollResult>
    abstract fun reroll(player: Player)

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