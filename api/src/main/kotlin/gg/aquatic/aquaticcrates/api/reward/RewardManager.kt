package gg.aquatic.aquaticcrates.api.reward

import org.bukkit.entity.Player

abstract class RewardManager {

    abstract val rewards: HashMap<String, Reward>
    abstract fun getRewards(player: Player): MutableList<RolledReward>

}