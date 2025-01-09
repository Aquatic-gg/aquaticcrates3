package gg.aquatic.aquaticcrates.api.animation.crate

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject

data class CrateAnimationActions(
    val animationActions: MutableList<ConfiguredExecutableObject<Animation,Unit>>,
    val playerBoundActions: MutableList<ConfiguredExecutableObject<PlayerBoundAnimation,Unit>>
) {
}