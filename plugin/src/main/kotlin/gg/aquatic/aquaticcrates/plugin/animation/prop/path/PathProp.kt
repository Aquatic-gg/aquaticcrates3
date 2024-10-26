package gg.aquatic.aquaticcrates.plugin.animation.prop.path

import org.bukkit.Location
import java.util.TreeMap

interface PathProp {

    val points: TreeMap<Int,PathPoint>
    val location: Location?

}