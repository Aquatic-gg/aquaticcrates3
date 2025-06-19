package gg.aquatic.aquaticcrates.plugin.editor

import gg.aquatic.aquaticcrates.plugin.editor.item.EditorItem
import org.bukkit.inventory.ItemStack

abstract class EditorCategory(
    val item: ItemStack
) {

    val items = ArrayList<Pair<String, EditorItem>>()
    abstract fun refresh(): EditorCategory
}