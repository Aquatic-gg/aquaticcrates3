package gg.aquatic.aquaticcrates.plugin.openprice

import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import gg.aquatic.aquaticcrates.api.player.PlayerHandler
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.price.AbstractPrice
import org.bukkit.entity.Player

class CrateKeyPrice: AbstractPrice<Player>() {
    override fun take(binder: Player, arguments: Map<String, Any?>) {
        val crateId = arguments["crate"] as String? ?: return
        CrateHandler.crates[crateId] ?: return

        PlayerHandler.takeKeys(binder, crateId, 1)
    }

    override fun arguments(): List<AquaticObjectArgument<*>> {
        return listOf(
            PrimitiveObjectArgument("crate", "", true)
        )
    }

    override fun set(binder: Player, arguments: Map<String, Any?>) {
    }

    override fun has(binder: Player, arguments: Map<String, Any?>): Boolean {
        val crateId = arguments["crate"] as String? ?: return false
        CrateHandler.crates[crateId] ?: return false

        return (PlayerHandler.totalKeys(binder, crateId) ?: 0) > 0
    }

    override fun give(binder: Player, arguments: Map<String, Any?>) {
    }
}