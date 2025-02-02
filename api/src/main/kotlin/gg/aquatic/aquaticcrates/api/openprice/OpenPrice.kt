package gg.aquatic.aquaticcrates.api.openprice

import gg.aquatic.waves.util.collection.executeActions
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import gg.aquatic.waves.util.price.ConfiguredPrice
import org.bukkit.entity.Player

class OpenPrice(
    val price: ConfiguredPrice<Player>,
    val failActions: MutableList<ConfiguredExecutableObject<Player,Unit>>
) {

    fun tryTake(player: Player): Boolean {
        if (price.has(player)) {
            price.take(player)
            return true
        }
        failActions.executeActions(player) { p, str -> str.replace("%player%", p.name) }
        return false
    }

}