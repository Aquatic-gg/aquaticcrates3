package gg.aquatic.aquaticcrates.plugin.reward

import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import org.bukkit.entity.Player

class RolledRewardImpl(override val reward: Reward, override val randomAmount: Int) : RolledReward() {
    override fun give(player: Player) {
        reward.give(player, randomAmount)
    }
}