package gg.aquatic.aquaticcrates.api.util

import gg.aquatic.aquaticseries.lib.item2.AquaticItem
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface ItemBased {

    val item: AquaticItem

    fun giveItem(amount: Int, vararg players: Player) {
        val itemStack = getItem(amount)

        for (player in players) {
            val iS = itemStack.clone()
            for ((_, item) in player.inventory.addItem(iS)) {
                player.world.dropItem(player.location, item)
            }
        }
    }

    fun getItem(amount: Int): ItemStack {
        val itemStack = item.getItem()
        itemStack.amount = amount

        return itemStack
    }

}