package gg.aquatic.aquaticcrates.plugin.animation.prop.inventory

import gg.aquatic.waves.inventory.InventoryType
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.menu.component.Button
import gg.aquatic.waves.scenario.PlayerScenario
import gg.aquatic.waves.scenario.PlayerScenarioProp
import gg.aquatic.waves.util.toMMComponent

class InventoryAnimationProp(
    override val scenario: PlayerScenario,
    title: String,
    size: Int,
    items: Map<Int, AquaticItem>
) : PlayerScenarioProp {

    val menu = AnimationMenu(
        scenario.updatePlaceholders(title).toMMComponent(),
        when (size) {
            54 -> InventoryType.GENERIC9X6
            45 -> InventoryType.GENERIC9X5
            36 -> InventoryType.GENERIC9X4
            27 -> InventoryType.GENERIC9X3
            18 -> InventoryType.GENERIC9X2
            9 -> InventoryType.GENERIC9X1
            else -> InventoryType.GENERIC9X6
        },
        scenario.player
    ).apply {
        for ((slot, item) in items) {
            components += "slot_$slot" to Button(
                "slot_$slot",
                item.getItem(),
                listOf(slot),
                1,
                1,
                null,
                { m -> true },
                { str, menu -> scenario.updatePlaceholders(str) },
                { e -> }
            )
        }
    }

    override fun tick() {

    }

    override fun onEnd() {
        menu.close()
    }
}