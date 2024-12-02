package gg.aquatic.aquaticcrates.plugin.reroll.input.inventory

import gg.aquatic.aquaticseries.lib.betterinventory2.SlotSelection
import gg.aquatic.waves.util.inventory.InventorySettings

class RerollInventorySettings(
    val inventorySettings: InventorySettings,
    val rewardSlots: SlotSelection,
    val clearBottomInventory: Boolean,
    val onClose: InventoryRerollInput.Action
) {
}