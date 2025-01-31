package gg.aquatic.aquaticcrates.api.animation.crate

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.waves.util.executeActions
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject

data class CrateAnimationActions(
    val animationActions: MutableList<ConfiguredExecutableObject<Animation, Unit>>,
    val playerBoundActions: MutableList<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>
) {

    fun execute(animation: Animation) {
        animationActions.executeActions(animation) { a, str -> a.updatePlaceholders(str) }
        if (animation is PlayerBoundAnimation) playerBoundActions.executeActions(animation) { a, str ->
            a.updatePlaceholders(
                str
            )
        }
    }

}