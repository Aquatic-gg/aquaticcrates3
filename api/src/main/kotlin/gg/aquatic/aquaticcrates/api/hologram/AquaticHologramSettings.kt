package gg.aquatic.aquaticcrates.api.hologram

import gg.aquatic.aquaticseries.lib.betterhologram.AquaticHologram
import org.bukkit.Location
import org.bukkit.util.Vector

class AquaticHologramSettings(
    val lines: MutableList<AquaticHologram.Line>,
    val offset: Vector,
    val billboard: AquaticHologram.Billboard
): HologramSettings {

    override fun create(location: Location): Hologram {
        return AHologram(location.clone(), this)
    }
}