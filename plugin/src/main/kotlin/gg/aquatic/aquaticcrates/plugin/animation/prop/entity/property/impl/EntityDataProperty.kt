package gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl

import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.EntityAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityPropertySerializer
import gg.aquatic.waves.fake.entity.FakeEntity
import gg.aquatic.waves.fake.entity.data.EntityData
import gg.aquatic.waves.registry.WavesRegistry
import org.bukkit.configuration.ConfigurationSection

class EntityDataProperty(
    val data: Collection<EntityData>
) : EntityProperty {
    override fun apply(entity: FakeEntity, prop: EntityAnimationProp) {
        entity.updateEntity {
            this.entityData += data.map { it.id to it }
        }
    }

    object Serializer : EntityPropertySerializer {
        override fun load(section: ConfigurationSection): EntityProperty {
            return EntityDataProperty(section.getKeys(false).mapNotNull { id ->
                val factory = WavesRegistry.ENTITY_PROPERTY_FACTORIES[id] ?: return@mapNotNull null
                factory.invoke(section) { str -> str }
            })
        }
    }
}