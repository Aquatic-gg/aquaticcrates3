package gg.aquatic.aquaticcrates.plugin.reroll

import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.reroll.RerollInput
import gg.aquatic.aquaticcrates.api.reroll.RerollManager
import gg.aquatic.aquaticcrates.api.reward.Reward
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

class RerollManagerImpl(override val crate: Crate, override val groups: HashMap<String, Int>, override val rerollInput: RerollInput) : RerollManager() {

    override fun openReroll(player: Player, rewards: List<Reward>): CompletableFuture<RerollResult> {
        return rerollInput.handle(this, player, rewards)
    }

    override fun reroll(player: Player) {
        TODO("Not yet implemented")
    }
}