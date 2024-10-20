package gg.aquatic.aquaticcrates.api.hologram

import org.bukkit.Location

interface HologramSettings {

    fun create(location: Location): Hologram

}