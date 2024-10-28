package gg.aquatic.aquaticcrates.plugin.animation.prop.path

import gg.aquatic.aquaticcrates.plugin.animation.prop.MovableAnimationProp
import java.util.TreeMap

interface PathProp {

    val points: TreeMap<Int,PathPoint>
    val currentPoint: PathPoint


    val boundProps: MutableMap<MovableAnimationProp,PathBoundProperties>

}