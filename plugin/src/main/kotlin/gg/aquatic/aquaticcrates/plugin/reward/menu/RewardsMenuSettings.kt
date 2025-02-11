package gg.aquatic.aquaticcrates.plugin.reward.menu

import gg.aquatic.waves.menu.settings.PrivateMenuSettings

class RewardsMenuSettings(
    val invSettings: PrivateMenuSettings,
    val rewardSlots: Collection<Int>,
    val additionalRewardLore: List<String>
) {
}