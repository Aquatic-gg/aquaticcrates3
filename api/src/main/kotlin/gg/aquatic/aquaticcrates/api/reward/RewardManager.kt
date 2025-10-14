package gg.aquatic.aquaticcrates.api.reward

import gg.aquatic.aquaticcrates.api.milestone.MilestoneManager
import org.bukkit.entity.Player
import java.util.TreeMap

abstract class RewardManager {

    abstract val guaranteedRewards: TreeMap<Int, Reward>
    abstract val milestoneManager: MilestoneManager
    abstract val rewards: MutableMap<String, Reward>
    abstract fun getRewards(player: Player): MutableList<RolledReward>
    abstract fun getPossibleRewards(player: Player): MutableMap<String,Reward>

}