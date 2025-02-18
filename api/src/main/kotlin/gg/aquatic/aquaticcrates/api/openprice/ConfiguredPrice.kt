package gg.aquatic.aquaticcrates.api.openprice

import gg.aquatic.waves.util.argument.ObjectArguments
import org.bukkit.entity.Player

class ConfiguredPrice(
    val args: ObjectArguments,
    val price: Price
) {

    fun take(player: Player, amount: Int) = price.take(
        player,
        args,
        amount
    )

    fun has(player: Player, amount: Int) = price.has(
        player,
        args,
        amount
    )

}