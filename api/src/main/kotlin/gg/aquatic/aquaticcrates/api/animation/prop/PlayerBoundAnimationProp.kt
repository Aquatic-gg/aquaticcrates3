package gg.aquatic.aquaticcrates.api.animation.prop

import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation

abstract class PlayerBoundAnimationProp: AnimationProp() {
    abstract override val animation: PlayerBoundAnimation
}