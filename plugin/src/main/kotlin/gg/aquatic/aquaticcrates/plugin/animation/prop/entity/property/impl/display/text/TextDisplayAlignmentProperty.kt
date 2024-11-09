package gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl.display.text

import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.EntityAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityPropertySerializer
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Entity
import org.bukkit.entity.TextDisplay.TextAlignment

class TextDisplayAlignmentProperty(
    val alignment: TextAlignment
): EntityProperty {
    override fun apply(entity: Entity, prop: EntityAnimationProp) {
        val display = entity as? org.bukkit.entity.TextDisplay ?: return
        display.alignment = alignment
    }

    object Serializer: EntityPropertySerializer {
        override fun load(section: ConfigurationSection): EntityProperty {
            return TextDisplayAlignmentProperty(TextAlignment.valueOf(section.getString("alignment", "CENTER")!!.uppercase()))
        }

    }
}