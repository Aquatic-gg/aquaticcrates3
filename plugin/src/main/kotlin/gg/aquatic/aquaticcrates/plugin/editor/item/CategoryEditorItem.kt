package gg.aquatic.aquaticcrates.plugin.editor.item

import gg.aquatic.aquaticcrates.plugin.editor.EditorCategory
import gg.aquatic.aquaticcrates.plugin.editor.menu.EditorMenu
import gg.aquatic.waves.inventory.event.AsyncPacketInventoryInteractEvent
import org.bukkit.inventory.ItemStack

class CategoryEditorItem(val category: EditorCategory) : EditorItem {

    override val itemStack: ItemStack = category.item

    override fun execute(event: AsyncPacketInventoryInteractEvent) {
        val previousMenu = event.inventory as EditorMenu
        //val previousCategory = previousMenu.category

        val newMenu = EditorMenu(previousMenu.crateModel, event.viewer.player, category, previousMenu)
        newMenu.open()
    }
}