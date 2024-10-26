package gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.name

import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.EntityAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.aquaticseries.lib.AquaticSeriesLib
import gg.aquatic.aquaticseries.lib.adapt.AquaticString
import org.bukkit.entity.Display
import org.bukkit.entity.Entity

class EntityNameProperty(
    val name: AquaticString
) : EntityProperty {
    override fun apply(entity: Entity) {
        if (entity is Display) {
            AquaticSeriesLib.INSTANCE.adapter.setDisplayText(entity, name)
        } else {
            name.setEntityName(entity)
        }
        //AquaticSeriesLib.INSTANCE.adapter
    }

    override fun applyAndUpdate(prop: EntityAnimationProp, entity: Entity) {
        apply(entity)
        AquaticSeriesLib.INSTANCE.nmsAdapter!!.updateEntity(prop.entityId, {}, prop.animation.audience)
    }
}