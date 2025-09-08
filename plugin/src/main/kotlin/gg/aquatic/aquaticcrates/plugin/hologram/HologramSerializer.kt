package gg.aquatic.aquaticcrates.plugin.hologram

import gg.aquatic.aquaticcrates.api.hologram.AquaticHologramSettings
import gg.aquatic.waves.hologram.HologramSerializer
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.util.Vector

object HologramSerializer {

    fun loadAquaticHologram(section: ConfigurationSection?): AquaticHologramSettings? {
        section ?: return null

        val offset = section.getString("offset", "0;1.5;0")!!.split(";")
        val vector = Vector(
            offset[0].toDouble(),
            offset[1].toDouble(),
            offset[2].toDouble()
        )

        val hologram = HologramSerializer.loadHologram(section)
        if (hologram.lines.isEmpty()) return null

        Bukkit.getConsoleSender().sendMessage("Loaded hologram with ${hologram.lines.size} lines")

        return AquaticHologramSettings(
            hologram,
            vector,
        )
    }

}