package gg.aquatic.aquaticcrates.api.hologram

import gg.aquatic.waves.hologram.AquaticHologram
import gg.aquatic.waves.hologram.HologramLine
import gg.aquatic.waves.util.audience.AquaticAudience
import gg.aquatic.waves.util.audience.GlobalAudience
import org.bukkit.Location
import org.bukkit.entity.Player

class AHologram(
    location: Location,
    val settings: AquaticHologramSettings
): Hologram(location) {

    var lines: MutableSet<HologramLine> = settings.lines.map { it.create() }.toMutableSet()
        private set

    private var audience: AquaticAudience = GlobalAudience()

    private var hologram: AquaticHologram? = null

    fun setLines(lines: List<HologramLine>, textUpdater: (Player,String) -> String) {
        this.lines = lines.toMutableSet()
        createHologram(textUpdater)
    }

    private fun createHologram(textUpdater: (Player,String) -> String) {
        hologram?.destroy()
        hologram = AquaticHologram(
            location.clone().add(settings.offset),
            { player: Player -> audience.canBeApplied(player) },
            textUpdater,
            50,
            lines
        )
    }

    override fun move(location: Location) {
        this.location = location
        hologram?.teleport(location.clone().add(settings.offset))
    }

    override fun despawn() {
        hologram?.destroy()
        hologram = null
    }

    override fun spawn(audience: AquaticAudience, textUpdater: (Player,String) -> String) {
        despawn()
        this.audience = audience
        createHologram(textUpdater)
    }

    override fun update(textUpdater: (Player,String) -> String) {
    }
}