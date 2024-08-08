package gg.aquatic.aquaticseries.item.factory.impl

import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import gg.aquatic.aquaticseries.item.impl.OraxenItem
import gg.aquatic.aquaticseries.item.CustomItem
import gg.aquatic.aquaticseries.item.factory.ItemFactory

object OraxenFactory: ItemFactory {

    override fun create(
        id: String,
        name: String?,
        description: MutableList<String>?,
        amount: Int,
        modelData: Int,
        enchantments: MutableMap<Enchantment, Int>?,
        flags: MutableList<ItemFlag>?
    ): CustomItem {
        return OraxenItem(id, name, description, amount, modelData, enchantments, flags)
    }

}