package gg.aquatic.aquaticcrates.plugin.pouch

import gg.aquatic.aquaticcrates.api.milestone.Milestone
import gg.aquatic.aquaticcrates.api.milestone.MilestoneManager
import java.util.*

class PouchMilestoneManager(
    override val milestones: TreeMap<Int, Milestone>,
    override val repeatableMilestones: TreeMap<Int, Milestone>
) : MilestoneManager() {
}