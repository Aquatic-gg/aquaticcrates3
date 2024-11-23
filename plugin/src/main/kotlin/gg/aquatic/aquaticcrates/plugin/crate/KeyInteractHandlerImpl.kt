package gg.aquatic.aquaticcrates.plugin.crate

import gg.aquatic.aquaticcrates.api.crate.Key
import gg.aquatic.aquaticcrates.api.crate.KeyInteractHandler
import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate
import org.bukkit.Location
import org.bukkit.entity.Player

class KeyInteractHandlerImpl(override val requiresCrateToOpen: Boolean, override val key: Key) : KeyInteractHandler() {
    override fun handleInteract(player: Player, isLeft: Boolean, interactedLocation: Location, crate: SpawnedCrate?) {
        player.sendMessage("You have interacted with the key!")
    }
}