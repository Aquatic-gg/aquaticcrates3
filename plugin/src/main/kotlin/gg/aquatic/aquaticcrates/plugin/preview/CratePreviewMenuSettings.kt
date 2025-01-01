package gg.aquatic.aquaticcrates.plugin.preview

import gg.aquatic.waves.menu.settings.PrivateMenuSettings

class CratePreviewMenuSettings(
    val invSettings: PrivateMenuSettings,
    val clearBottomInventory: Boolean,
    val rewardSlots: Collection<Int>,
    val randomRewards: RandomRewardsSettings,
    val additionalRewardLore: List<String>,
    val updateRewardItemsEvery: Int
) {

    class RandomRewardsSettings(
        val slots: Collection<Int>,
        val changeDuration: Int
    )

}