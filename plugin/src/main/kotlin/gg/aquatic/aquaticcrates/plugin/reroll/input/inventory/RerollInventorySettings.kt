package gg.aquatic.aquaticcrates.plugin.reroll.input.inventory

import gg.aquatic.waves.menu.SlotSelection
import gg.aquatic.waves.menu.settings.PrivateMenuSettings


class RerollInventorySettings(
    val inventorySettings: PrivateMenuSettings,
    val rewardSlots: SlotSelection,
    val clearBottomInventory: Boolean,
    val onClose: InventoryRerollInput.Action
) {
}