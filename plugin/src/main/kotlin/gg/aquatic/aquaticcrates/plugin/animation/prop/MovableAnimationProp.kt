package gg.aquatic.aquaticcrates.plugin.animation.prop

import org.bukkit.Location
import org.bukkit.util.Vector

interface MovableAnimationProp {

    val boundLocationOffset: Vector?

    fun move(location: Location)

}