package gg.aquatic.aquaticcrates.plugin.hologram

import gg.aquatic.aquaticcrates.api.hologram.AquaticHologramSettings
import gg.aquatic.waves.hologram.HologramSerializer
import gg.aquatic.waves.util.getSectionList
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.util.Vector

object HologramSerializer {

    fun loadAquaticHologram(section: ConfigurationSection?): AquaticHologramSettings {
        section ?: return AquaticHologramSettings(
            HashSet(),
            Vector(),
        )

        val offset = section.getString("offset", "0;0;0")!!.split(";")
        val vector = Vector(
            offset[0].toDouble(),
            offset[1].toDouble(),
            offset[2].toDouble()
        )

        val lines = HologramSerializer.loadLines(section.getSectionList("lines"))
        Bukkit.getConsoleSender().sendMessage("Loaded hologram with ${lines.size} lines")

        return AquaticHologramSettings(
            lines,
            vector,
        )
    }

}