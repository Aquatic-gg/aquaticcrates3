package gg.aquatic.aquaticcrates.plugin

import gg.aquatic.aquaticcrates.api.player.PlayerHandler
import org.bukkit.entity.Player

fun Player.takeKeys(id: String, amount: Int): Boolean {
    return PlayerHandler.takeKeys(this,id,amount)
    /*
    var currentAmount = 0
    val items = ArrayList<ItemStack>()
    for (storageContent in this.inventory.storageContents) {
        val key = Key.get(storageContent) ?: continue
        if (key.crate.identifier != id) continue
        if (storageContent.amount < amount) continue
        items.add(storageContent)
        currentAmount+=storageContent.amount
        if (currentAmount >= amount) {
            currentAmount=amount
            break
        }
    }
    if (currentAmount < amount) return false
    for (item in items) {
        if (item.amount > currentAmount) {
            item.amount-=currentAmount
            return true
        }
        val toRemove = item.amount
        item.amount = 0
        currentAmount-=toRemove
        if (currentAmount == 0) return true
    }
    return true
     */
}
