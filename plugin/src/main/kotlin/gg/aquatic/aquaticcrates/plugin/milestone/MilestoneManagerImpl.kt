package gg.aquatic.aquaticcrates.plugin.milestone

import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.milestone.Milestone
import gg.aquatic.aquaticcrates.api.milestone.MilestoneManager
import java.util.*

class MilestoneManagerImpl(
    val crate: OpenableCrate,
    override val milestones: TreeMap<Int, Milestone>,
    override val repeatableMilestones: TreeMap<Int, Milestone>
) : MilestoneManager() {
}