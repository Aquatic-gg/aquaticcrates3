package gg.aquatic.aquaticcrates.plugin.animation.prop.inventory

import gg.aquatic.waves.inventory.InventoryType
import gg.aquatic.waves.menu.PrivateAquaticMenu
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class AnimationMenu(title: Component, type: InventoryType, player: Player) : PrivateAquaticMenu(title, type, player, true) {

    var closed = false
    fun close() {
        closed = true
        player.closeInventory()
    }

}