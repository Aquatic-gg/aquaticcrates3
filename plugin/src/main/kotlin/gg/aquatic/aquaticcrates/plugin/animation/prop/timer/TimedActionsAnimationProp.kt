package gg.aquatic.aquaticcrates.plugin.animation.prop.timer

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationActions
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp

class TimedActionsAnimationProp(override val animation: Animation, val actions: HashMap<Int, CrateAnimationActions>) :
    AnimationProp() {

    var tick = 0
        private set

    override fun tick() {
        actions[tick]?.execute(animation)
        tick++
    }

    override fun onAnimationEnd() {
    }
}