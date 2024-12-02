package gg.aquatic.aquaticcrates.api.player

import gg.aquatic.aquaticcrates.api.crate.Key
import gg.aquatic.waves.profile.toAquaticPlayer
import gg.aquatic.waves.util.readEntityMetadata
import org.bukkit.entity.Player

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

}