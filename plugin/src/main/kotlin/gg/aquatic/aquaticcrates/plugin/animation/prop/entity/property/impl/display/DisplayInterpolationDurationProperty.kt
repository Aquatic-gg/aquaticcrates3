package gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl.display

import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.EntityAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityPropertySerializer
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Entity

class DisplayInterpolationDurationProperty(
    val duration: Int
): EntityProperty {
    override fun apply(entity: Entity, prop: EntityAnimationProp) {
        val display = entity as? org.bukkit.entity.Display ?: return
        display.interpolationDuration = duration
    }

    object Serializer : EntityPropertySerializer {
        override suspend fun load(section: ConfigurationSection): EntityProperty {
            val duration = section.getInt("interpolation-duration", 0)
            return DisplayInterpolationDurationProperty(duration)
        }

    }
}