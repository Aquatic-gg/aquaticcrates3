package gg.aquatic.aquaticcrates.plugin.editor.item

import gg.aquatic.aquaticcrates.api.util.createItem
import gg.aquatic.aquaticcrates.plugin.editor.menu.EditorMenu
import gg.aquatic.waves.inventory.event.AsyncPacketInventoryInteractEvent
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ToggleEditorItem(
    val currentState: Boolean,
    val name: String, val lore: List<String>,
    val dataApplier: (Boolean) -> Unit
) : EditorItem {

    override val itemStack: ItemStack
        get() {
            val item = createItem(
                if (currentState) {
                    Material.REDSTONE_TORCH
                } else Material.LEVER
            ) {
                displayName = name
                val modifiedLore = ArrayList<String>()
                modifiedLore += this@ToggleEditorItem.lore
                modifiedLore += "Current state: $currentState"
                lore = modifiedLore
            }
            return item
        }

    override fun execute(event: AsyncPacketInventoryInteractEvent) {
        dataApplier(!currentState)

        val menu = event.inventory as EditorMenu
        val newMenu = EditorMenu(menu.crateModel, event.viewer.player, menu.category.refresh(), menu.previousMenu)
        newMenu.open()
    }
}