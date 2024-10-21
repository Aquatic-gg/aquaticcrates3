package gg.aquatic.aquaticcrates.api.openprice

import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import gg.aquatic.aquaticseries.lib.util.executeActions
import org.bukkit.entity.Player

class OpenPriceGroup(
    val prices: MutableList<OpenPrice>,
    val failActions: MutableList<ConfiguredAction<Player>>
) {

    fun tryTake(player: Player): Boolean {
        for (price in prices) {
            if (price.tryTake(player)) return true
        }
        failActions.executeActions(player) { p, str -> str.replace("%player%", p.name) }
        return false
    }
}