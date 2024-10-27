package gg.aquatic.aquaticcrates.plugin.pouch

import gg.aquatic.aquaticcrates.api.pouch.PouchInteractHandler
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player

class PouchInteractHandlerImpl(override val pouch: RewardPouch) : PouchInteractHandler() {
    override fun handleInteract(player: Player, isLeft: Boolean, interactedLocation: Location) {
        if (!isLeft) {
            pouch.tryOpen(player, interactedLocation)
            return
        } else {
            pouch.openPreview(player)
            Bukkit.broadcastMessage("Opening preview!")
        }
    }
}