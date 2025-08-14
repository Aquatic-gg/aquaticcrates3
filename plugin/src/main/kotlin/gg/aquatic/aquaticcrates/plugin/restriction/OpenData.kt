package gg.aquatic.aquaticcrates.plugin.restriction

import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import org.bukkit.Location
import org.bukkit.entity.Player

class OpenData(
    val player: Player,
    val location: Location?,
    val crate: OpenableCrate
) {
}