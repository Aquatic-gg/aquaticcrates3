package gg.aquatic.aquaticcrates.plugin.animation.prop.inventory

import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.api.animation.prop.PlayerBoundAnimationProp
import gg.aquatic.waves.inventory.InventoryType
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.menu.component.Button
import gg.aquatic.waves.util.toMMComponent

class InventoryAnimationProp(
    override val animation: PlayerBoundAnimation,
    title: String,
    size: Int,
    items: Map<Int, AquaticItem>
) : PlayerBoundAnimationProp() {

    val menu = AnimationMenu(
        animation.updatePlaceholders(title).toMMComponent(),
        when (size) {
            54 -> InventoryType.GENERIC9X6
            45 -> InventoryType.GENERIC9X5
            36 -> InventoryType.GENERIC9X4
            27 -> InventoryType.GENERIC9X3
            18 -> InventoryType.GENERIC9X2
            9 -> InventoryType.GENERIC9X1
            else -> InventoryType.GENERIC9X6
        },
        animation.player
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
                { str, menu -> animation.updatePlaceholders(str) },
                { e -> }
            )
        }
    }

    override fun tick() {

    }

    override fun onAnimationEnd() {
        menu.close()
    }
}