package gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property

import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl.EntityArmorProperty
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

    override suspend fun load(section: ConfigurationSection): List<EntityProperty>? {
        Bukkit.getConsoleSender().sendMessage("Loading properties!")
        return serializer.load(section, id)
    }

    object Serializer : AbstractObjectArgumentSerializer<List<EntityProperty>?>() {

        val factories = mutableMapOf(
            "display-name" to EntityNameProperty.Serializer,
            "display-name-visible" to EntityNameVisibleProperty.Serializer,
            "armor" to EntityArmorProperty.Serializer,
        )

        override suspend fun load(section: ConfigurationSection, id: String): List<EntityProperty> {
            val properties = mutableListOf<EntityProperty>()

            val s = section.getConfigurationSection(id) ?: return properties
            Bukkit.getConsoleSender().sendMessage("Properties path: ${s.currentPath}")

            for ((key, factory) in factories) {
                Bukkit.getConsoleSender().sendMessage("Loading property: $key")
                if (s.contains(key)) {
                    properties += factory.load(s)
                    Bukkit.getConsoleSender().sendMessage("Loaded property: $key")
                }
            }
            return properties
        }
    }
}