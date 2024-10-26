package gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property

import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.name.EntityNamePropertySerializer
import gg.aquatic.aquaticseries.lib.util.argument.AbstractObjectArgumentSerializer
import gg.aquatic.aquaticseries.lib.util.argument.AquaticObjectArgument
import org.bukkit.configuration.ConfigurationSection

class EntityPropertiesObjectArgument(
    id: String,
    defaultValue: List<EntityProperty>?, required: Boolean
) : AquaticObjectArgument<List<EntityProperty>>(id, defaultValue, required) {
    override val serializer: AbstractObjectArgumentSerializer<List<EntityProperty>?> = Serializer

    override suspend fun load(section: ConfigurationSection): List<EntityProperty>? {
        return serializer.load(section, id)
    }

    object Serializer : AbstractObjectArgumentSerializer<List<EntityProperty>?>() {

        val factories = mutableMapOf<String, EntityPropertySerializer>(
            "display-name" to EntityNamePropertySerializer
        )

        override suspend fun load(section: ConfigurationSection, id: String): List<EntityProperty> {
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