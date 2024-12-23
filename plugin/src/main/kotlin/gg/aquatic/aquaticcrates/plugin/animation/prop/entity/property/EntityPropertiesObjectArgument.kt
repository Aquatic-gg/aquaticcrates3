package gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property

import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl.EntityArmorProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl.EntityDataProperty
import gg.aquatic.waves.util.argument.AbstractObjectArgumentSerializer
import gg.aquatic.waves.util.argument.AquaticObjectArgument
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
            "armor" to EntityArmorProperty.Serializer,
            "data" to EntityDataProperty.Serializer,

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