package gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl.display.item

import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.EntityAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityPropertySerializer
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Entity
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform

class ItemDisplayTransformProperty(
    val transform: ItemDisplayTransform
): EntityProperty {
    override fun apply(entity: Entity, prop: EntityAnimationProp) {
        val display = entity as? org.bukkit.entity.ItemDisplay ?: return
        display.itemDisplayTransform = transform
    }

    object Serializer: EntityPropertySerializer {
        override suspend fun load(section: ConfigurationSection): EntityProperty {
            return ItemDisplayTransformProperty(ItemDisplayTransform.valueOf(section.getString("item-transform", "NONE")!!.uppercase()))
        }

    }
}