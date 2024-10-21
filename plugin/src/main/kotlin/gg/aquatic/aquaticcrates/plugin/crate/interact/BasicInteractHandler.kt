package gg.aquatic.aquaticcrates.plugin.crate.interact

import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.crate.InteractHandler
import org.bukkit.entity.Player

class BasicInteractHandler(override val crate: Crate) : InteractHandler() {
    override fun handleInteract(player: Player, isLeft: Boolean) {

    }
}