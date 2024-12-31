package gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl

import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.EntityAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityPropertySerializer
import gg.aquatic.waves.fake.entity.FakeEntity
import gg.aquatic.waves.packetevents.EntityDataBuilder
import gg.aquatic.waves.util.getSectionList
import org.bukkit.configuration.ConfigurationSection

class EntityDataProperty(
    val data: List<gg.aquatic.waves.interactable.settings.entityproperty.EntityProperty>
): EntityProperty {
    override fun apply(entity: FakeEntity, prop: EntityAnimationProp) {
        val builder = EntityDataBuilder.ANY
        entity.updateEntity {
            for (datum in data) {
                datum.apply(builder)
            }
        }
        for (entityData in builder.build()) {
            entity.entityData += entityData.index to entityData
        }
    }

    object Serializer : EntityPropertySerializer {
        override fun load(section: ConfigurationSection): EntityProperty {
            return EntityDataProperty(gg.aquatic.waves.registry.serializer.EntityPropertySerializer.fromSections(section.getSectionList("properties")))
        }
    }
}