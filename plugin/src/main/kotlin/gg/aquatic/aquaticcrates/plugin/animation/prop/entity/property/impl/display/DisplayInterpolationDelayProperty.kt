package gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl.display

import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.EntityAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityPropertySerializer
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Display
import org.bukkit.entity.Entity

class DisplayInterpolationDelayProperty(
    val delay: Int
): EntityProperty {
    override fun apply(entity: Entity, prop: EntityAnimationProp) {
        val display = entity as? Display ?: return
        display.interpolationDelay = delay
    }

    object Serializer : EntityPropertySerializer {
        override fun load(section: ConfigurationSection): EntityProperty {
            val delay = section.getInt("interpolation-delay", 0)
            return DisplayInterpolationDelayProperty(delay)
        }

    }
}