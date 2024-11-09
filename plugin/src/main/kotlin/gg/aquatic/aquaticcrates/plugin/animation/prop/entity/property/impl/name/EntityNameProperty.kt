package gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl.name

import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.EntityAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityPropertySerializer
import gg.aquatic.aquaticseries.lib.AquaticSeriesLib
import gg.aquatic.aquaticseries.lib.adapt.AquaticString
import gg.aquatic.aquaticseries.lib.util.toAquatic
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Entity
import org.bukkit.entity.TextDisplay

class EntityNameProperty(
    val name: AquaticString
) : EntityProperty {
    override fun apply(entity: Entity, prop: EntityAnimationProp) {
        if (entity is TextDisplay) {
            AquaticSeriesLib.INSTANCE.adapter.setDisplayText(entity, prop.animation.updatePlaceholders(name.string).toAquatic())
        } else {
            prop.animation.updatePlaceholders(name.string).toAquatic().setEntityName(entity)
        }
    }

    object Serializer : EntityPropertySerializer {
        override fun load(section: ConfigurationSection): EntityProperty {
            return EntityNameProperty(section.getString("display-name", "")!!.toAquatic())
        }
    }
}