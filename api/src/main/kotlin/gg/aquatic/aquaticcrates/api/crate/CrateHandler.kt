package gg.aquatic.aquaticcrates.api.crate

import gg.aquatic.aquaticcrates.api.pouch.Pouch
import org.bukkit.Location

object CrateHandler {

    val crates = HashMap<String, Crate>()
    val spawned = HashMap<Location, SpawnedCrate>()

    val pouches = HashMap<String, Pouch>()

    fun spawnCrate(crate: Crate, location: Location): SpawnedCrate {
        val spawnedCrate = SpawnedCrate(crate, location)
        val blockLoc = location.block.location.clone()
        spawned[blockLoc] = spawnedCrate
        return spawnedCrate
    }

}