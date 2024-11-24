package gg.aquatic.aquaticcrates.api.reward

import gg.aquatic.aquaticcrates.api.milestone.MilestoneManager
import org.bukkit.entity.Player

abstract class RewardManager {

    abstract val milestoneManager: MilestoneManager
    abstract val rewards: HashMap<String, Reward>
    abstract fun getRewards(player: Player): MutableList<RolledReward>
    abstract fun getPossibleRewards(player: Player): HashMap<String,Reward>

}