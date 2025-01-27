package gg.aquatic.aquaticcrates.plugin.animation.prop

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationActions
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.waves.util.executeActions

class TimedActionsAnimationProp(override val animation: Animation, val actions: HashMap<Int, CrateAnimationActions>) :
    AnimationProp() {

    var tick = 0
        private set

    override fun tick() {
        actions[tick]?.animationActions?.executeActions(animation) { _, str ->
            animation.updatePlaceholders(str)
        }
        if (animation is PlayerBoundAnimation) actions[tick]?.playerBoundActions?.executeActions(animation) { _, str ->
            animation.updatePlaceholders(str)
        }
        tick++
    }

    override fun onAnimationEnd() {
    }
}