package gg.aquatic.aquaticcrates.plugin.reroll

import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.reroll.RerollInput
import gg.aquatic.aquaticcrates.api.reroll.RerollManager
import gg.aquatic.aquaticcrates.api.reward.Reward
import org.bukkit.entity.Player

class RerollManagerImpl(override val crate: Crate, override val groups: HashMap<String, Int>) : RerollManager() {
    override val rerollInput: RerollInput
        get() = TODO("Not yet implemented")

    override fun openReroll(player: Player, reward: Reward) {
        TODO("Not yet implemented")
    }

    override fun reroll(player: Player) {
        TODO("Not yet implemented")
    }
}