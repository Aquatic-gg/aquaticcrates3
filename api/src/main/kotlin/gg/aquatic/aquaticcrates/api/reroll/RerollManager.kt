package gg.aquatic.aquaticcrates.api.reroll

import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.reward.Reward
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

abstract class RerollManager(
) {

    abstract val crate: Crate
    abstract val groups: HashMap<String,Int>
    abstract val rerollInput: RerollInput

    abstract fun openReroll(player: Player, rewards: Collection<Reward>): CompletableFuture<RerollResult>
    abstract fun reroll(player: Player)

    class RerollResult(
        val reroll: Boolean
    )
}