package gg.aquatic.aquaticcrates.plugin.preview

import gg.aquatic.waves.menu.settings.PrivateMenuSettings

class CratePreviewMenuSettings(
    val invSettings: PrivateMenuSettings,
    val clearBottomInventory: Boolean,
    val rewardSlots: List<Int>,
    val randomRewards: RandomRewardsSettings
) {

    class RandomRewardsSettings(
        val slots: List<Int>,
        val changeDuration: Int
    )

}