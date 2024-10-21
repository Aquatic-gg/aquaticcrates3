package gg.aquatic.aquaticcrates.plugin.crate.key

import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.crate.Key
import gg.aquatic.aquaticseries.lib.item2.AquaticItem
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class KeyImpl(
    crate: Crate,
    item: AquaticItem,
    override val requiresCrateToOpen: Boolean,
    override val mustBeHeld: Boolean
) : Key(crate, item) {

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