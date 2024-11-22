package gg.aquatic.aquaticcrates.plugin.hologram

import gg.aquatic.aquaticcrates.api.hologram.AquaticHologramSettings
import gg.aquatic.aquaticseries.lib.betterhologram.AquaticHologram
import gg.aquatic.aquaticseries.lib.betterhologram.impl.ArmorstandLine
import gg.aquatic.aquaticseries.lib.betterhologram.impl.TextDisplayLine
import gg.aquatic.aquaticseries.lib.util.getSectionList
import gg.aquatic.aquaticseries.lib.util.updatePAPIPlaceholders
import gg.aquatic.waves.registry.serializer.HologramSerializer
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.function.BiFunction

object HologramSerializer {

    fun loadAquaticHologram(section: ConfigurationSection?): AquaticHologramSettings {
        section ?: return AquaticHologramSettings(
            ArrayList(),
            Vector(),
            AquaticHologram.Billboard.CENTER
        )

        val offset = section.getString("offset", "0;0;0")!!.split(";")
        val billboard = AquaticHologram.Billboard.valueOf(section.getString("billboard", "CENTER")!!.uppercase())
        val vector = Vector(
            offset[0].toDouble(),
            offset[1].toDouble(),
            offset[2].toDouble()
        )
        val lines = HologramSerializer.load(section.getSectionList("lines"))
        for (line in lines) {
            if (line is TextDisplayLine) {
                line.textUpdater = BiFunction<Player, String, String> { t, u -> u.updatePAPIPlaceholders(t) }
            } else if (line is ArmorstandLine) {
                line.textUpdater = BiFunction<Player, String, String> { t, u -> u.updatePAPIPlaceholders(t) }
            }
        }
        return AquaticHologramSettings(lines, vector, billboard)
    }

}