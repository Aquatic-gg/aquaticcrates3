package gg.aquatic.aquaticcrates.plugin.crate.key

import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.crate.Key
import gg.aquatic.aquaticseries.lib.item2.AquaticItem
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class KeyImpl(
    override val crate: Crate,
    override val item: AquaticItem,
    override val requiresCrateToOpen: Boolean,
    override val mustBeHeld: Boolean
) : Key() {

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

        val meta = itemStack.itemMeta
        meta!!.persistentDataContainer.set(NAMESPACED_KEY, PersistentDataType.STRING,crate.identifier)
        itemStack.itemMeta = meta

        return itemStack
    }

    fun isItemKey(item: ItemStack): Boolean {
        val meta = item.itemMeta
        return meta!!.persistentDataContainer.has(NAMESPACED_KEY, PersistentDataType.STRING) && meta.persistentDataContainer.get(NAMESPACED_KEY, PersistentDataType.STRING) == crate.identifier
    }
}