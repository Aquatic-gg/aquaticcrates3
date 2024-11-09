package gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl.name

import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.EntityAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityPropertySerializer
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Entity

class EntityNameVisibleProperty(
    val nameVisible: Boolean
): EntityProperty {
    override fun apply(entity: Entity, prop: EntityAnimationProp) {
        entity.isCustomNameVisible = nameVisible
    }

    object Serializer : EntityPropertySerializer {
        override fun load(section: ConfigurationSection): EntityProperty {
            return EntityNameVisibleProperty(section.getBoolean("display-name-visible", false))
        }
    }
}