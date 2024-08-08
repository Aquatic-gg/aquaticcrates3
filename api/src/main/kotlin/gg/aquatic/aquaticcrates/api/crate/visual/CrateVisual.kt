package gg.aquatic.aquaticcrates.api.crate.visual

import gg.aquatic.aquaticcrates.api.crate.Crate
import org.bukkit.Location
import org.bukkit.entity.Player

abstract class CrateVisual(
) {

    abstract val crate: Crate
    abstract val location: Location

    abstract fun spawn(player: Player)
    abstract fun despawn(player: Player)

    abstract fun handler(): VisualHandler

}