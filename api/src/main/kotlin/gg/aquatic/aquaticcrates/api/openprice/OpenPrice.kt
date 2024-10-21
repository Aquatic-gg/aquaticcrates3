package gg.aquatic.aquaticcrates.api.openprice

import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import gg.aquatic.aquaticseries.lib.price.ConfiguredPrice
import gg.aquatic.aquaticseries.lib.util.executeActions
import org.bukkit.entity.Player

class OpenPrice(
    val price: ConfiguredPrice<Player>,
    val failActions: MutableList<ConfiguredAction<Player>>
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