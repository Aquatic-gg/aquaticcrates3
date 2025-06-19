package gg.aquatic.aquaticcrates.plugin.editor.category.collection

import gg.aquatic.aquaticcrates.plugin.editor.EditorCategory
import gg.aquatic.aquaticcrates.plugin.editor.item.EditorItem
import gg.aquatic.aquaticcrates.plugin.editor.menu.EditorMenu
import gg.aquatic.waves.inventory.ButtonType
import gg.aquatic.waves.inventory.event.AsyncPacketInventoryInteractEvent
import org.bukkit.inventory.ItemStack

class CollectionEditorCategory<T>(
    itemStack: ItemStack,
    val collection: MutableList<T>,
    val itemMapper: (Int, T) -> Pair<String, EditorItem>,
    val addItem: EditorItem
): EditorCategory(itemStack) {

    override fun refresh(): EditorCategory {
        return CollectionEditorCategory(item, collection, itemMapper, addItem)
    }

    init {
        items += collection.mapIndexed { i, item -> itemMapper(i, item) }
        items += "add" to addItem
    }

    class CollectionEditorItem(
        val index: Int,
        val editorItem: EditorItem
    ): EditorItem {
        override val itemStack: ItemStack
            get() {
                return editorItem.itemStack
            }

        override fun execute(event: AsyncPacketInventoryInteractEvent) {
            event.viewer.player.sendMessage("Clicked $index, btn type: ${event.buttonType}")
            if (event.buttonType == ButtonType.SHIFT_RIGHT) {
                val player = event.viewer.player
                val menu = event.inventory as EditorMenu

                val currentCategory = menu.category as CollectionEditorCategory<*>
                currentCategory.collection.removeAt(index)
                val newMenu = EditorMenu(menu.crateModel, player, currentCategory.refresh(), menu.previousMenu)
                newMenu.open()
            } else {
                editorItem.execute(event)
            }
        }

    }

}