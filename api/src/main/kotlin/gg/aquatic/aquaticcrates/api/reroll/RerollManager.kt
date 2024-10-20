package gg.aquatic.aquaticcrates.api.reroll

import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.reward.Reward
import org.bukkit.entity.Player

abstract class RerollManager(
) {

    abstract val crate: Crate
    abstract val groups: HashMap<String,Int>
    abstract val rerollInput: RerollInput

    abstract fun openReroll(player: Player, reward: Reward)
    abstract fun reroll(player: Player)

}