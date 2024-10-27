package gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property

import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.EntityAnimationProp
import org.bukkit.entity.Entity

interface EntityProperty {

    fun apply(entity: Entity, prop: EntityAnimationProp)
}