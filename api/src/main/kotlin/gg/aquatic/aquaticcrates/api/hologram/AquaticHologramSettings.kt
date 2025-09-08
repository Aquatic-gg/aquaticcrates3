package gg.aquatic.aquaticcrates.api.hologram

import gg.aquatic.waves.hologram.AquaticHologram
import org.bukkit.Location
import org.bukkit.util.Vector

class AquaticHologramSettings(
    val hologram: AquaticHologram.Settings,
    val offset: Vector,
): HologramSettings {

    override fun create(location: Location): Hologram {
        return AHologram(location.clone(), this)
    }
}