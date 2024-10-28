package gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl

import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.EntityAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityPropertySerializer
import org.bukkit.entity.Entity

class EntityGlowProperty(val isGlowing: Boolean): EntityProperty {
    override fun apply(entity: Entity, prop: EntityAnimationProp) {
        entity.isGlowing = isGlowing
    }

    object Serializer: EntityPropertySerializer {
        override suspend fun load(section: org.bukkit.configuration.ConfigurationSection): EntityProperty {
            return EntityGlowProperty(section.getBoolean("glow", false))
        }
    }
}