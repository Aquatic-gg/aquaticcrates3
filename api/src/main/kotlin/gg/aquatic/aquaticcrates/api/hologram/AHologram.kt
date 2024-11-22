package gg.aquatic.aquaticcrates.api.hologram

import gg.aquatic.aquaticseries.lib.audience.AquaticAudience
import gg.aquatic.aquaticseries.lib.audience.GlobalAudience
import gg.aquatic.aquaticseries.lib.betterhologram.AquaticHologram
import gg.aquatic.aquaticseries.lib.betterhologram.AquaticHologram.Anchor
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.function.Consumer

class AHologram(
    location: Location,
    val settings: AquaticHologramSettings
): Hologram(location) {


    var lines: MutableList<AquaticHologram.Line> = settings.lines.toMutableList()

    private var audience: AquaticAudience = GlobalAudience()

    private var hologram: AquaticHologram? = null

    fun setLines(lines: List<AquaticHologram.Line>) {
        despawn()
        this.lines = ArrayList(lines)
        createHologram()
        hologram?.update()
    }

    private fun createHologram() {
        val lines = ArrayList<AquaticHologram.Line>()
        for (line in this.lines) {
            lines.add(line.clone())
        }
        hologram = AquaticHologram(
            { player: Player -> audience.canBeApplied(player) },
            null,
            lines,
            Anchor.MIDDLE,
            settings.billboard,
            location.clone().add(settings.offset),
            50.0
        )
        HologramHandler.holograms.add(hologram ?: return)
    }

    override fun move(location: Location) {
        this.location = location
        hologram?.move(location.clone().add(settings.offset))
    }

    override fun despawn() {
        HologramHandler.holograms.remove(hologram ?: return)
        hologram?.despawn()
        hologram = null
    }

    override fun spawn(audience: AquaticAudience, consumer: Consumer<List<String>>) {
        despawn()
        this.audience = audience
        createHologram()
        hologram?.update()
    }

    override fun update(consumer: Consumer<List<String>>) {
    }
}