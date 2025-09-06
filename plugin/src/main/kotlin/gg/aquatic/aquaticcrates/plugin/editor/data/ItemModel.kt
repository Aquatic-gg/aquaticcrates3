package gg.aquatic.aquaticcrates.plugin.editor.data

import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.item.ItemHandler
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

data class ItemModel(
    var material: String,
    var amount: Int,
    var displayName: String?,
    var lore: MutableList<String> = mutableListOf(),
) {

    companion object {
        fun of(aquaticItem: AquaticItem): ItemModel {
            return ItemModel(
                aquaticItem.internalId!!,
                aquaticItem.amount,
                aquaticItem.name,
                aquaticItem.lore.toMutableList()
            )
        }

        // TODO: ADD ITEM RESOLVERS
    }

    val aquaticItem: AquaticItem
        get() {
            val typeId = if (material.contains(":")) {
                material.split(":")[0].uppercase()
            } else null

            if (typeId == null) {
                return ItemHandler.create(
                    material,
                    ItemStack.of(Material.valueOf(material.uppercase())),
                    displayName,
                    lore.toMutableList(),
                    amount,
                    listOf()
                )
            }
            // TODO: ADD FACTORIES
            throw Exception("Invalid item type $typeId")
        }

}