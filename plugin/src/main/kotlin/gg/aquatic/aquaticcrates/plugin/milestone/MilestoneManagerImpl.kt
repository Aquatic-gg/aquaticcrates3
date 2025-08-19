package gg.aquatic.aquaticcrates.plugin.milestone

import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.milestone.Milestone
import gg.aquatic.aquaticcrates.api.milestone.MilestoneManager
import gg.aquatic.aquaticcrates.api.player.CrateProfileEntry
import gg.aquatic.aquaticcrates.api.player.HistoryHandler
import org.bukkit.entity.Player
import java.util.*
import kotlin.math.max

class MilestoneManagerImpl(
    val crate: OpenableCrate,
    override val milestones: TreeMap<Int, Milestone>,
    override val repeatableMilestones: TreeMap<Int, Milestone>
) : MilestoneManager() {

    override fun milestonesReached(player: Player): List<Milestone> {
        val milestonesReached = mutableListOf<Milestone>()
        val totalOpened = HistoryHandler.history(crate.identifier, CrateProfileEntry.HistoryType.ALLTIME, player) + 1
        milestones[totalOpened]?.let {
            milestonesReached += it
        }
        repeatableMilestones.forEach { (key, value) ->
            if (totalOpened % key == 0) {
                milestonesReached += value
            }
        }
        return milestonesReached
    }

    override fun remaining(player: Player, milestone: Int): Int {
        if (milestone < 1) return 0
        val totalOpened = HistoryHandler.history(crate.identifier, CrateProfileEntry.HistoryType.ALLTIME, player)
        return max(milestone - totalOpened, 0)
    }

    override fun remainingRepeatable(player: Player, milestone: Int): Int {
        if (milestone < 1) return 0
        val totalOpened = HistoryHandler.history(crate.identifier, CrateProfileEntry.HistoryType.ALLTIME, player)
        return max(milestone - (totalOpened % milestone), milestone)
    }
}