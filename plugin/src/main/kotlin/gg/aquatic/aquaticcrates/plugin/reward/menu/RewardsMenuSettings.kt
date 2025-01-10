package gg.aquatic.aquaticcrates.plugin.reward.menu

import gg.aquatic.waves.inventory.InventoryType
import gg.aquatic.waves.menu.MenuComponent

class RewardsMenuSettings(
    val title: String,
    val type: InventoryType,
    val components: Map<String, MenuComponent>,
    val rewardSlots: Collection<Int>
) {
}