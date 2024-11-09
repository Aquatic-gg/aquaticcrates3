package gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl

import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.EntityAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityPropertySerializer
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity

class EntityInvisibilityProperty(
    val invisible: Boolean
): EntityProperty {
    override fun apply(entity: Entity, prop: EntityAnimationProp) {
        if (entity is LivingEntity) {
            entity.isInvisible = invisible
        }
    }

    object Serializer : EntityPropertySerializer {
        override fun load(section: ConfigurationSection): EntityProperty {
            return EntityInvisibilityProperty(section.getBoolean("invisible", false))
        }
    }

}