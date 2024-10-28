package gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl.display.text

import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.EntityAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityPropertySerializer
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Entity

class TextDisplayLineWidthProperty(
    val width: Int
): EntityProperty {
    override fun apply(entity: Entity, prop: EntityAnimationProp) {
        val display = entity as? org.bukkit.entity.TextDisplay ?: return
        display.lineWidth = width
    }

    object Serializer: EntityPropertySerializer {
        override suspend fun load(section: ConfigurationSection): EntityProperty {
            val width = section.getInt("line-width", 50)
            return TextDisplayLineWidthProperty(width)
        }

    }
}