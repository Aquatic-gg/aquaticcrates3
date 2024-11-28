package gg.aquatic.aquaticcrates.plugin.milestone

import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.milestone.Milestone
import gg.aquatic.aquaticcrates.api.milestone.MilestoneManager
import gg.aquatic.aquaticcrates.api.player.CrateProfileEntry
import gg.aquatic.aquaticcrates.api.player.HistoryHandler
import org.bukkit.entity.Player
import java.util.*

class MilestoneManagerImpl(
    val crate: OpenableCrate,
    override val milestones: TreeMap<Int, Milestone>,
    override val repeatableMilestones: TreeMap<Int, Milestone>
) : MilestoneManager() {

    override fun milestonesReached(player: Player): List<Milestone> {
        val milestonesReached = mutableListOf<Milestone>()
        val totalOpened = HistoryHandler.history("crate:${crate.identifier}", CrateProfileEntry.HistoryType.ALLTIME, player)
        if (milestones.containsKey(totalOpened)) {
            milestonesReached += milestones[totalOpened]!!
        }
        repeatableMilestones.forEach { (key, value) ->
            if (totalOpened % key == 0) {
                milestonesReached += value
            }
        }
        return milestonesReached
    }
}