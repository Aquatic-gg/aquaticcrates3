package gg.aquatic.aquaticcrates.plugin.pouch

import gg.aquatic.aquaticseries.lib.betterinventory2.serialize.InventorySettings

class PouchPreviewMenuSettings(
    val invSettings: InventorySettings?,
    val clearBottomInventory: Boolean,
    val rewardSlots: List<Int>
) {
}