package gg.aquatic.aquaticcrates.api.openprice.impl

import gg.aquatic.aquaticcrates.api.openprice.Price
import gg.aquatic.aquaticcrates.api.player.PlayerHandler
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import org.bukkit.entity.Player

class CrateKeyPrice: Price {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("crate", "", true),
        PrimitiveObjectArgument("amount", 1, false)
    )

    override fun take(player: Player, arguments: ObjectArguments, amount: Int) {
        val crateId = arguments.string("crate") ?: return
        val originalAmount = arguments.int("amount") ?: 1
        PlayerHandler.takeKeys(player, crateId, amount*originalAmount)
    }

    override fun has(player: Player, arguments: ObjectArguments, amount: Int): Boolean {
        return (PlayerHandler.totalKeys(player, arguments.string("crate") ?: return false) ?: return false) >= amount
    }
}