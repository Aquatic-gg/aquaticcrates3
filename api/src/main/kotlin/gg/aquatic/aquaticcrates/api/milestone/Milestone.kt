package gg.aquatic.aquaticcrates.api.milestone

import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticseries.lib.adapt.AquaticString

interface Milestone {
    val milestone: Int
    val displayName: AquaticString
    val rewards: List<Reward>
}