package gg.aquatic.aquaticcrates.plugin.preview

import gg.aquatic.waves.menu.settings.PrivateMenuSettings

class CratePreviewMenuSettings(
    val invSettings: PrivateMenuSettings,
    val clearBottomInventory: Boolean,
    val rewardSlots: Collection<Int>,
    val randomRewards: RandomRewardsSettings
) {

    class RandomRewardsSettings(
        val slots: Collection<Int>,
        val changeDuration: Int
    )

}