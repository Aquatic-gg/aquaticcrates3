package gg.aquatic.aquaticcrates.plugin.animation.prop

import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathBoundProperties
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathPoint
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathProp
import org.bukkit.Location
import org.bukkit.util.Vector

interface MovableAnimationProp {

    val boundPaths: MutableMap<PathProp,PathBoundProperties>
    val processedPaths: MutableList<PathProp>

    //val boundLocationOffset: Vector?

    fun processPath(
        path: PathProp,
        point: PathPoint)
    fun move(location: Location)

}