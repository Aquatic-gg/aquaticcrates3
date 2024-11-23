package gg.aquatic.aquaticcrates.api.crate

import gg.aquatic.aquaticseries.lib.audience.GlobalAudience
import org.bukkit.Location

class SpawnedCrate(
    val crate: Crate,
    val location: Location
) {

    val spawnedInteractables = crate.interactables.map {
        it.build(location, GlobalAudience()) { e ->
            crate.interactHandler.handleInteract(e.player, e.isLeft, location, this)
        }
    }
}