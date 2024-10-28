package gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl.display.text

import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.EntityAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityPropertySerializer
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Entity

class TextDisplaySeeThroughProperty(val isSeeThrough: Boolean): EntityProperty {
    override fun apply(entity: Entity, prop: EntityAnimationProp) {
        val display = entity as? org.bukkit.entity.TextDisplay ?: return
        display.isSeeThrough = isSeeThrough
    }

    object Serializer: EntityPropertySerializer {
        override suspend fun load(section: ConfigurationSection): EntityProperty {
            return TextDisplaySeeThroughProperty(section.getBoolean("see-through", false))
        }

    }
}