package gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot
import gg.aquatic.aquaticcrates.api.util.animationitem.ArgumentItem
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.EntityAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityPropertySerializer
import gg.aquatic.waves.fake.entity.FakeEntity
import gg.aquatic.waves.packetevents.EntityDataBuilder
import gg.aquatic.waves.util.collection.mapPair
import org.bukkit.configuration.ConfigurationSection

class EntityArmorProperty(
    val helmet: ArgumentItem?,
    val chestplate: ArgumentItem?,
    val leggings: ArgumentItem?,
    val boots: ArgumentItem?,
) : EntityProperty {
    override fun apply(entity: FakeEntity, prop: EntityAnimationProp) {

        if (entity.type == EntityTypes.ITEM) {
            entity.updateEntity {
                helmet?.getActualItem(prop.animation)?.getItem()?.let {
                    entityData += EntityDataBuilder.ITEM().setItem(it).build()
                        .mapPair { value -> value.index to value }
                }
            }
        }
        else if (entity.type != EntityTypes.ITEM_DISPLAY) {
            entity.updateEntity {
                helmet?.getActualItem(prop.animation)?.getItem()?.let {
                    equipment[EquipmentSlot.HELMET] = it
                }
                chestplate?.getActualItem(prop.animation)?.getItem()?.let {
                    equipment[EquipmentSlot.CHEST_PLATE] = it
                }
                leggings?.getActualItem(prop.animation)?.getItem()?.let {
                    equipment[EquipmentSlot.LEGGINGS] = it
                }
                boots?.getActualItem(prop.animation)?.getItem()?.let {
                    equipment[EquipmentSlot.BOOTS] = it
                }
            }
            return
        } else {
            entity.updateEntity {
                helmet?.getActualItem(prop.animation)?.getItem()?.let {
                    entityData += EntityDataBuilder.ITEM_DISPLAY().setItem(it).build()
                        .mapPair { value -> value.index to value }
                }
            }
        }
    }

    object Serializer : EntityPropertySerializer {
        override fun load(section: ConfigurationSection): EntityProperty {
            val helmet = ArgumentItem.loadFromYml(section.getConfigurationSection("armor.helmet"))
            val chestplate = ArgumentItem.loadFromYml(section.getConfigurationSection("armor.chestplate"))
            val leggings = ArgumentItem.loadFromYml(section.getConfigurationSection("armor.leggings"))
            val boots = ArgumentItem.loadFromYml(section.getConfigurationSection("armor.boots"))
            return EntityArmorProperty(helmet, chestplate, leggings, boots)
        }
    }
}