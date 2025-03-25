package gg.aquatic.aquaticcrates.plugin.reward

import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.waves.util.runSync
import org.bukkit.entity.Player

class RolledRewardImpl(override val reward: Reward, override val randomAmount: Int) : RolledReward() {
    override fun give(player: Player, massOpen: Boolean) {
        if (reward.asyncExecution) {
            reward.give(player, randomAmount, massOpen)
        } else {
            runSync {
                reward.give(player, randomAmount, massOpen)
            }
        }
    }
}