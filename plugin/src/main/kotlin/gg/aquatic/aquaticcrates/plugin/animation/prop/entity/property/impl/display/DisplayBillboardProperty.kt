package gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl.display

import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.EntityAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityPropertySerializer
import org.bukkit.entity.Display.Billboard
import org.bukkit.entity.Entity

class DisplayBillboardProperty(
    val billboard: Billboard
): EntityProperty {
    override fun apply(entity: Entity, prop: EntityAnimationProp) {
        val display = entity as? org.bukkit.entity.Display ?: return
        display.billboard = billboard
    }

    object Serializer : EntityPropertySerializer {
        override suspend fun load(section: org.bukkit.configuration.ConfigurationSection): EntityProperty {
            return DisplayBillboardProperty(Billboard.valueOf(section.getString("billboard", "CENTER")!!.uppercase()))
        }
    }
}