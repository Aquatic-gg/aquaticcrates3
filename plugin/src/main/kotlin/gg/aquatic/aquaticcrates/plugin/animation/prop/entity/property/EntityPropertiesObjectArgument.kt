package gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property

import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl.EntityArmorProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl.EntityDataProperty
import gg.aquatic.waves.util.argument.AbstractObjectArgumentSerializer
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import org.bukkit.configuration.ConfigurationSection

class EntityPropertiesObjectArgument(
    id: String,
    defaultValue: List<EntityProperty>?, required: Boolean, aliases: Collection<String> = listOf()
) : AquaticObjectArgument<List<EntityProperty>>(id, defaultValue, required, aliases) {
    override val serializer: AbstractObjectArgumentSerializer<List<EntityProperty>?> = Serializer

    object Serializer : AbstractObjectArgumentSerializer<List<EntityProperty>?>() {

        val factories = mutableMapOf(
            "armor" to EntityArmorProperty.Serializer,
        )

        override fun load(section: ConfigurationSection, id: String): List<EntityProperty> {
            val properties = mutableListOf<EntityProperty>()
            val s = section.getConfigurationSection(id) ?: return properties

            properties += EntityDataProperty.Serializer.load(s)
            for ((key, factory) in factories) {
                if (s.contains(key)) {
                    properties += factory.load(s)
                }
            }
            return properties
        }
    }
}