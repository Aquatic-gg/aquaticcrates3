package gg.aquatic.aquaticcrates.api.openprice

import gg.aquatic.waves.util.collection.executeActions
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import org.bukkit.entity.Player

class OpenPrice(
    val price: ConfiguredPrice,
    val failActions: MutableList<ConfiguredExecutableObject<Player,Unit>>
) {

    fun tryTake(player: Player, amount: Int): Boolean {
        if (price.has(player, amount)) {
            price.take(player, amount)
            return true
        }
        failActions.executeActions(player) { p, str -> str.replace("%player%", p.name) }
        return false
    }

}