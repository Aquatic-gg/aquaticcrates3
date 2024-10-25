package gg.aquatic.aquaticcrates.api.animation.prop

import gg.aquatic.aquaticcrates.api.animation.Animation

abstract class AnimationProp {

    abstract val animation: Animation

    abstract fun tick()
    abstract fun onAnimationEnd()

}