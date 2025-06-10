package gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl

import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.EntityAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityPropertySerializer
import gg.aquatic.waves.fake.entity.FakeEntity
import gg.aquatic.waves.fake.entity.data.ConfiguredEntityData
import gg.aquatic.waves.registry.WavesRegistry
import gg.aquatic.waves.util.argument.ArgumentSerializer
import gg.aquatic.waves.util.argument.ObjectArguments
import org.bukkit.configuration.ConfigurationSection

class EntityDataProperty(
    val data: Collection<ConfiguredEntityData>
) : EntityProperty {
    override fun apply(entity: FakeEntity, prop: EntityAnimationProp) {
        entity.updateEntity {
            val generated = data.flatMap { it.generate { str -> prop.animation.updatePlaceholders(str) } }
            setEntityData(generated)
        }
    }

    object Serializer : EntityPropertySerializer {
        override fun load(section: ConfigurationSection): EntityProperty {
            return EntityDataProperty(section.getKeys(false).mapNotNull { id ->
                val type = WavesRegistry.ENTITY_DATA[id] ?: return@mapNotNull null
                val arguments = ObjectArguments(ArgumentSerializer.load(section,type.arguments))
                ConfiguredEntityData(type,arguments)
            })
        }
    }
}