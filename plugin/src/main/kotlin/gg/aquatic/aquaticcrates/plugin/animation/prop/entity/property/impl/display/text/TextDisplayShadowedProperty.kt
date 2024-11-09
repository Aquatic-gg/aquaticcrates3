package gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl.display.text

import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.EntityAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityPropertySerializer
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Entity

class TextDisplayShadowedProperty(
    val shadow: Boolean
): EntityProperty {
    override fun apply(entity: Entity, prop: EntityAnimationProp) {
        val display = entity as? org.bukkit.entity.TextDisplay ?: return
        display.isShadowed = shadow
    }

    object Serializer: EntityPropertySerializer {
        override fun load(section: ConfigurationSection): EntityProperty {
            return TextDisplayShadowedProperty(section.getBoolean("shadowed", true))
        }

    }
}