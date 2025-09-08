package gg.aquatic.aquaticcrates.api.hologram

import gg.aquatic.waves.hologram.AquaticHologram
import gg.aquatic.waves.util.audience.AquaticAudience
import gg.aquatic.waves.util.audience.GlobalAudience
import org.bukkit.Location
import org.bukkit.entity.Player

class AHologram(
    location: Location,
    val settings: AquaticHologramSettings
) : Hologram(location) {

    val hologramSettings = settings.hologram

    private var audience: AquaticAudience = GlobalAudience()

    private var hologram: AquaticHologram? = null

    private fun createHologram(textUpdater: (Player, String) -> String) {
        hologram?.destroy()
        hologram = hologramSettings.create(
            location.clone().add(settings.offset),
            textUpdater
        ) { p -> audience.canBeApplied(p) }
    }

    override fun move(location: Location) {
        this.location = location
        hologram?.teleport(location.clone().add(settings.offset))
    }

    override fun despawn() {
        hologram?.destroy()
        hologram = null
    }

    override fun spawn(audience: AquaticAudience, textUpdater: (Player, String) -> String) {
        despawn()
        this.audience = audience
        createHologram(textUpdater)
    }
}