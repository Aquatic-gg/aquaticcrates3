package gg.aquatic.aquaticcrates.plugin.animation.prop.path

import org.bukkit.util.Vector

class PathPoint(
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float,
    val pitch: Float,
) {

    val vector: Vector
        get() = Vector(x, y, z)

}