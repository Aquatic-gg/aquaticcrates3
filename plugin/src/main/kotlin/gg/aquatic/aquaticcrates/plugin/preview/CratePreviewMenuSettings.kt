package gg.aquatic.aquaticcrates.plugin.preview

import gg.aquatic.waves.util.inventory.InventorySettings

class CratePreviewMenuSettings(
    val invSettings: InventorySettings,
    val clearBottomInventory: Boolean,
    val rewardSlots: List<Int>,
    val randomRewards: RandomRewardsSettings
) {

    class RandomRewardsSettings(
        val slots: List<Int>,
        val changeDuration: Int
    )

}