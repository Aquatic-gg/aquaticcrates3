package gg.aquatic.aquaticcrates.api.crate

import gg.aquatic.aquaticseries.lib.util.AquaticLocation
import gg.aquatic.aquaticseries.lib.util.Config
import org.bukkit.Location
import org.bukkit.World
import java.util.concurrent.ConcurrentHashMap

object CrateHandler {

    val crates = ConcurrentHashMap<String, Crate>()
    val cratesToSpawn = ConcurrentHashMap<String, HashMap<AquaticLocation, Crate>>()
    val spawned = ConcurrentHashMap<Location, SpawnedCrate>()

    fun spawnCrate(crate: Crate, location: Location): SpawnedCrate {
        val spawnedCrate = SpawnedCrate(crate, location)
        val blockLoc = location.block.location.clone()
        spawned[blockLoc] = spawnedCrate
        return spawnedCrate
    }

    fun saveSpawnedCrates(config: Config) {
        config.load()
        val cfg = config.getConfiguration()!!
        val mapped = HashMap<String, MutableList<String>>()
        for ((location, sc) in spawned) {
            mapped.getOrPut(location.world!!.name) { ArrayList() } += "${location.x}:${location.y}:${location.z}:${location.yaw}:${sc.crate.identifier}"
        }
        for ((key, list) in mapped) {
            cfg.set(key, list)
        }
        config.save()
    }

    fun loadSpawnedCrates(config: Config) {
        config.load()
        val cfg = config.getConfiguration()!!
        for (key in cfg.getKeys(false)) {
            val list = cfg.getStringList(key)
            for (str in list) {
                val strs = str.split(":")
                val loc = AquaticLocation(
                    key,
                    strs[0].toDoubleOrNull() ?: continue,
                    strs[1].toDoubleOrNull() ?: continue,
                    strs[2].toDoubleOrNull() ?: continue,
                    strs[3].toFloatOrNull() ?: continue,
                    0f
                )

                val crate = crates[strs[4]] ?: continue

                val location = loc.toLocation()
                if (location != null) {
                    spawnCrate(crate, location)
                    continue
                }
                cratesToSpawn.getOrPut(key) { HashMap() }[loc] = crate
            }
        }
    }

    fun onWorldLoad(world: World) {
        val crates = cratesToSpawn[world.name] ?: return
        for ((location, crate) in crates) {
            val loc = location.toLocation() ?: continue
            spawnCrate(crate, loc)
        }
    }
}