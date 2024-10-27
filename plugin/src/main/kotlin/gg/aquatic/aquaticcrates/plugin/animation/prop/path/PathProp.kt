package gg.aquatic.aquaticcrates.plugin.animation.prop.path

import gg.aquatic.aquaticcrates.plugin.animation.prop.MovableAnimationProp
import org.bukkit.Location
import java.util.TreeMap

interface PathProp {

    val points: TreeMap<Int,PathPoint>
    val location: Location?


    val boundProps: MutableList<MovableAnimationProp>

}