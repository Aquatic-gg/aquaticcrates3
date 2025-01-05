package gg.aquatic.aquaticcrates.api.hologram

import gg.aquatic.waves.hologram.LineSettings
import org.bukkit.Location
import org.bukkit.util.Vector

class AquaticHologramSettings(
    val lines: Set<LineSettings>,
    val offset: Vector,
): HologramSettings {

    override fun create(location: Location): Hologram {
        return AHologram(location.clone(), this)
    }
}