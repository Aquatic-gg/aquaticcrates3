package gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl.display.text

import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.EntityAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityPropertySerializer
import org.bukkit.Color
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Entity

class TextDisplayBackgroundProperty(
    val isDefaultBackground: Boolean,
    val backgroundColor: Color?
): EntityProperty {
    override fun apply(entity: Entity, prop: EntityAnimationProp) {
        val display = entity as? org.bukkit.entity.TextDisplay ?: return

        display.isDefaultBackground = isDefaultBackground
        display.backgroundColor = backgroundColor
    }

    object Serializer: EntityPropertySerializer {
        override fun load(section: ConfigurationSection): EntityProperty {
            val bgSection = section.getConfigurationSection("background") ?: return TextDisplayBackgroundProperty(true, null)
            val isDefaultBackground = bgSection.getBoolean("is-default-background", true)

            val a = bgSection.getInt("background-color.a", 255)
            val r = bgSection.getInt("background-color.a", 255)
            val g = bgSection.getInt("background-color.a", 255)
            val b = bgSection.getInt("background-color.a", 255)

            val color = Color.fromARGB(a,r,g,b)
            return TextDisplayBackgroundProperty(isDefaultBackground, color)
        }

    }


}