package gg.aquatic.aquaticcrates.plugin.pouch

import gg.aquatic.aquaticcrates.api.pouch.Pouch
import gg.aquatic.aquaticcrates.api.pouch.PouchInteractHandler
import org.bukkit.entity.Player

class PouchInteractHandlerImpl(override val pouch: Pouch) : PouchInteractHandler() {
    override fun handleInteract(player: Player, isLeft: Boolean) {

    }
}