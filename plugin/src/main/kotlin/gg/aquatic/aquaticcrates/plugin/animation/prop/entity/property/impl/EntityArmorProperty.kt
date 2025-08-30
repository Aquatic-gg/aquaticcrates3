package gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl

import gg.aquatic.aquaticcrates.api.util.animationitem.ArgumentItem
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.EntityAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityPropertySerializer
import gg.aquatic.waves.api.nms.entity.EntityDataValue
import gg.aquatic.waves.fake.entity.FakeEntity
import gg.aquatic.waves.fake.entity.data.impl.ItemEntityData
import gg.aquatic.waves.fake.entity.data.impl.display.ItemDisplayEntityData
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.EntityType
import org.bukkit.inventory.EquipmentSlot

class EntityArmorProperty(
    val helmet: ArgumentItem?,
    val chestplate: ArgumentItem?,
    val leggings: ArgumentItem?,
    val boots: ArgumentItem?,
) : EntityProperty {
    override fun apply(entity: FakeEntity, prop: EntityAnimationProp) {
        if (helmet == null && chestplate == null && leggings == null && boots == null) {
            return
        }
        if (entity.type == EntityType.ITEM) {
            entity.updateEntity {
                helmet?.getActualItem(prop.scenario)?.getItem()?.let {
                    val data = ArrayList<EntityDataValue>()
                    if (entity.type == EntityType.ITEM) {
                        data += ItemEntityData.Item.generate(it)
                    }
                    setEntityData(data)
                }
            }
        } else if (entity.type != EntityType.ITEM_DISPLAY) {
            entity.updateEntity {
                helmet?.getActualItem(prop.scenario)?.getItem()?.let {
                    equipment[EquipmentSlot.HEAD] = it
                }
                chestplate?.getActualItem(prop.scenario)?.getItem()?.let {
                    equipment[EquipmentSlot.CHEST] = it
                }
                leggings?.getActualItem(prop.scenario)?.getItem()?.let {
                    equipment[EquipmentSlot.LEGS] = it
                }
                boots?.getActualItem(prop.scenario)?.getItem()?.let {
                    equipment[EquipmentSlot.FEET] = it
                }
            }
            return
        } else {
            entity.updateEntity {
                helmet?.getActualItem(prop.scenario)?.getItem()?.let {
                    val data = ArrayList<EntityDataValue>()
                    if (entity.type == EntityType.ITEM_DISPLAY) {
                        data += ItemDisplayEntityData.Item.generate(it)
                    }
                    setEntityData(data)
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