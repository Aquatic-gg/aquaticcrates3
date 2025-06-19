package gg.aquatic.aquaticcrates.api.util

import gg.aquatic.waves.util.toMMComponent
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ItemBuilder(
    val material: Material
) {

    var displayName: String? = null
    var lore: List<String>? = null
    var amount: Int = 1
    var customModelData: Int? = null

    fun build(): ItemStack {
        val item = ItemStack(material, amount)
        val meta = item.itemMeta
        displayName?.toMMComponent()?.let {
            meta.displayName(it)
        }
        lore?.map { it.toMMComponent() }?.let {
            meta.lore(it)
        }
        meta.setCustomModelData(customModelData)
        item.itemMeta = meta
        return item
    }
}

inline fun createItem(material: Material, builder: ItemBuilder.() -> Unit): ItemStack {
    return ItemBuilder(material).apply(builder).build()
}
