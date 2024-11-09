package gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property

import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl.EntityArmorProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl.EntityGlowProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl.EntityInvisibilityProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl.display.DisplayBillboardProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl.display.DisplayInterpolationDelayProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl.display.DisplayInterpolationDurationProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl.display.DisplayTransformProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl.display.item.ItemDisplayTransformProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl.display.text.*
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl.name.EntityNameProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl.name.EntityNameVisibleProperty
import gg.aquatic.aquaticseries.lib.util.argument.AbstractObjectArgumentSerializer
import gg.aquatic.aquaticseries.lib.util.argument.AquaticObjectArgument
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection

class EntityPropertiesObjectArgument(
    id: String,
    defaultValue: List<EntityProperty>?, required: Boolean
) : AquaticObjectArgument<List<EntityProperty>>(id, defaultValue, required) {
    override val serializer: AbstractObjectArgumentSerializer<List<EntityProperty>?> = Serializer

    override fun load(section: ConfigurationSection): List<EntityProperty>? {
        return serializer.load(section, id)
    }

    object Serializer : AbstractObjectArgumentSerializer<List<EntityProperty>?>() {

        val factories = mutableMapOf(
            "display-name" to EntityNameProperty.Serializer,
            "display-name-visible" to EntityNameVisibleProperty.Serializer,
            "armor" to EntityArmorProperty.Serializer,
            "invisible" to EntityInvisibilityProperty.Serializer,
            "glowing" to EntityGlowProperty.Serializer,
            "interpolation-delay" to DisplayInterpolationDelayProperty.Serializer,
            "interpolation-duration" to DisplayInterpolationDurationProperty.Serializer,
            "billboard" to DisplayBillboardProperty.Serializer,
            "transformation" to DisplayTransformProperty.Serializer,
            "line-width" to TextDisplayLineWidthProperty.Serializer,
            "alignment" to TextDisplayAlignmentProperty.Serializer,
            "background" to TextDisplayBackgroundProperty.Serializer,
            "see-through" to TextDisplaySeeThroughProperty.Serializer,
            "shadowed" to TextDisplayShadowedProperty.Serializer,
            "item-transform" to ItemDisplayTransformProperty.Serializer,

        )

        override fun load(section: ConfigurationSection, id: String): List<EntityProperty> {
            val properties = mutableListOf<EntityProperty>()
            val s = section.getConfigurationSection(id) ?: return properties
            for ((key, factory) in factories) {
                if (s.contains(key)) {
                    properties += factory.load(s)
                }
            }
            return properties
        }
    }
}