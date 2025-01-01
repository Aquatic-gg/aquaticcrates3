package gg.aquatic.aquaticcrates.plugin.preview

import gg.aquatic.waves.menu.settings.PrivateMenuSettings

class CratePreviewMenuSettings(
    val invSettings: PrivateMenuSettings,
    val clearBottomInventory: Boolean,
    val rewardSlots: Set<Int>,
    val randomRewards: RandomRewardsSettings
) {

    class RandomRewardsSettings(
        val slots: Set<Int>,
        val changeDuration: Int
    )

}