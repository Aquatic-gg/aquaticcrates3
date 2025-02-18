package gg.aquatic.aquaticcrates.api.milestone

import org.bukkit.entity.Player
import java.util.TreeMap

abstract class MilestoneManager {

    abstract val milestones: TreeMap<Int, Milestone>
    abstract val repeatableMilestones: TreeMap<Int, Milestone>
    abstract fun milestonesReached(player: Player): List<Milestone>
    abstract fun remaining(player: Player, milestone: Int): Int
    abstract fun remainingRepeatable(player: Player, milestone: Int): Int

}