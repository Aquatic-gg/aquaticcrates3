package gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property

import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.EntityAnimationProp
import gg.aquatic.waves.fake.entity.FakeEntity
import org.bukkit.entity.Entity

interface EntityProperty {

    fun apply(entity: FakeEntity, prop: EntityAnimationProp)
}