package gg.aquatic.aquaticcrates.plugin

import gg.aquatic.aquaticcrates.api.crate.Key
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun Player.physicalKeys(id: String): Int {
    var total = 0
    for (storageContent in this.inventory.storageContents) {
        val key = Key.get(storageContent) ?: continue
        if (key.crate.identifier != id) continue
        total+=storageContent.amount
    }
    return total
}

fun Player.hasPhysicalKey(id: String, amount: Int): Boolean {
    return this.physicalKeys(id) >= amount
}

fun Player.takePhysicalKeys(id: String, amount: Int): Boolean {
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
}
