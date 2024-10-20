package gg.aquatic.aquaticcrates.api.milestone

import java.util.TreeMap

abstract class MilestoneManager {

    abstract val milestones: TreeMap<Int, Milestone>
    abstract val repeatableMilestones: TreeMap<Int, Milestone>

}