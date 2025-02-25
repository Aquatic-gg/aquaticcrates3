package gg.aquatic.aquaticcrates.plugin.animation.prop

import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.EntityAnimationProp

interface Seatable {

    fun addPassenger(entityAnimationProp: EntityAnimationProp)
    fun removePassenger(entityAnimationProp: EntityAnimationProp)
}