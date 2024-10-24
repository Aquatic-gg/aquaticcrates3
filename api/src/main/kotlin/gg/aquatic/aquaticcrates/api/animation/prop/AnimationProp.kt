package gg.aquatic.aquaticcrates.api.animation.prop

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation

abstract class AnimationProp {

    abstract val animation: CrateAnimation

    abstract fun tick()
    abstract fun onAnimationEnd()

}