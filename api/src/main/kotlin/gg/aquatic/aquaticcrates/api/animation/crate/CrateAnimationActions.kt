package gg.aquatic.aquaticcrates.api.animation.crate

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.waves.util.collection.executeActions
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject

data class CrateAnimationActions(
    val animationActions: MutableCollection<ConfiguredExecutableObject<Animation, Unit>>,
    val playerBoundActions: MutableCollection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>,
) {

    constructor() : this(mutableListOf(), mutableListOf())

    fun execute(animation: Animation) {
        animationActions.executeActions(animation) { a, str -> a.updatePlaceholders(str) }
        if (animation is PlayerBoundAnimation) {
            playerBoundActions.executeActions(animation) { a, str ->
                a.updatePlaceholders(
                    str
                )
            }
        }
    }

}