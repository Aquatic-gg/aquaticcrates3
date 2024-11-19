package gg.aquatic.aquaticcrates.plugin.crate.interact

import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.crate.CrateInteractHandler
import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate
import org.bukkit.Location
import org.bukkit.entity.Player

class BasicCrateInteractHandler(override val crate: Crate) : CrateInteractHandler() {

    override fun handleInteract(player: Player, isLeft: Boolean, interactedLocation: Location, crate: SpawnedCrate?) {
        player.sendMessage("You have interacted the crate!")
    }

}