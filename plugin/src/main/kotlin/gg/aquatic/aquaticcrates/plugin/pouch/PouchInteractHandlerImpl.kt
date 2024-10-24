package gg.aquatic.aquaticcrates.plugin.pouch

import gg.aquatic.aquaticcrates.api.pouch.Pouch
import gg.aquatic.aquaticcrates.api.pouch.PouchInteractHandler
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player

class PouchInteractHandlerImpl(override val pouch: Pouch) : PouchInteractHandler() {
    override fun handleInteract(player: Player, isLeft: Boolean, interactedLocation: Location) {
        if (!isLeft) {
            pouch.open(player, interactedLocation, false)
            Bukkit.broadcastMessage("Pouch is being opened!")
            return
        }
    }
}