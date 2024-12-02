package gg.aquatic.aquaticcrates.api.player

import gg.aquatic.aquaticcrates.api.crate.Key
import gg.aquatic.waves.profile.toAquaticPlayer
import gg.aquatic.waves.util.readEntityMetadata
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object PlayerHandler {

    fun virtualKeys(player: Player, id: String): Int? {
        return player.toAquaticPlayer()?.crateEntry()?.balance(id)
    }

    fun physicalKeys(player: Player, key: Key): Int? {
        player.toAquaticPlayer()?.crateEntry() ?: return null
        var amt = 0
        for (stack in player.inventory.storageContents) {
            if (Key.get(stack) == key) {
                amt += stack.amount
            }
        }
        return amt
    }

    fun totalKeys(player: Player, key: Key): Int? {
        val vKeys = virtualKeys(player,key.crate.identifier) ?: return null
        val pKeys = physicalKeys(player, key) ?: return null
        return vKeys + pKeys
    }

    /*
    fun takeKeys(player: Player, key: Key, amount: Int): Boolean {
        val entry = player.toAquaticPlayer()?.crateEntry() ?: return false
        var remaining = amount
        val vKeys = virtualKeys(player, key.crate.identifier) ?: return false
        val pKeys = physicalKeys(player, key) ?: return false
        if (vKeys + pKeys < amount) {
            return false
        }
        if (vKeys < amount) {
            entry.take(amount - vKeys, key.crate.identifier)
            remaining -= vKeys
        } else {
            entry.take(amount, key.crate.identifier)
            return true
        }
        for (stack in player.inventory.storageContents) {
            val keyStack = Key.get(stack) ?: continue
            if (keyStack == key) {
                if (stack.amount > remaining) {
                    stack.amount -= remaining
                    return true
                }
                remaining -= stack.amount
                stack.amount = 0
            }
        }
        return true
    }
     */

    fun takeKeys(player: Player, id: String, amount: Int): Boolean {
        var currentAmount = virtualKeys(player, id) ?: return false
        val items = ArrayList<ItemStack>()
        for (storageContent in player.inventory.storageContents) {
            val pkey = Key.get(storageContent) ?: continue
            if (pkey.crate.identifier != id) continue
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
        if (currentAmount > 0) {
            val entry = player.toAquaticPlayer()?.crateEntry() ?: return false
            entry.take(currentAmount, key.crate.identifier)
        }
        return true
    }

}