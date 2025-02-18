package gg.aquatic.aquaticcrates.api.milestone

import gg.aquatic.aquaticcrates.api.reward.Reward
import net.kyori.adventure.text.Component

class Milestone(
    val milestone: Int,
    val displayName: Component,
    val rewards: List<Reward>,
) {

}