package gg.aquatic.aquaticcrates.api.openprice

import gg.aquatic.waves.util.collection.executeActions
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import org.bukkit.entity.Player

class OpenPriceGroup(
    val prices: MutableList<OpenPrice>,
    val failActions: MutableList<ConfiguredExecutableObject<Player,Unit>>
) {

    fun tryTake(player: Player): Boolean {
        for (price in prices) {
            if (price.tryTake(player)) return true
        }
        failActions.executeActions(player) { p, str -> str.replace("%player%", p.name) }
        return false
    }
}