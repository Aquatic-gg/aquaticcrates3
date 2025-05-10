package gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl

import gg.aquatic.aquaticcrates.api.util.animationitem.ArgumentItem
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.EntityAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityPropertySerializer
import gg.aquatic.waves.fake.entity.FakeEntity
import gg.aquatic.waves.fake.entity.data.EntityData
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Item
import org.bukkit.entity.ItemDisplay
import org.bukkit.inventory.EquipmentSlot

class EntityArmorProperty(
    val helmet: ArgumentItem?,
    val chestplate: ArgumentItem?,
    val leggings: ArgumentItem?,
    val boots: ArgumentItem?,
) : EntityProperty {
    override fun apply(entity: FakeEntity, prop: EntityAnimationProp) {

        if (entity.type == EntityType.ITEM) {
            entity.updateEntity {
                helmet?.getActualItem(prop.animation)?.getItem()?.let {
                    entityData += "item" to object : EntityData {
                        override val id: String
                            get() = "item"

                        override fun apply(entity: Entity) {
                            if (entity !is Item) return
                            entity.itemStack = it
                        }

                    }
                }
            }
        } else if (entity.type != EntityType.ITEM_DISPLAY) {
            entity.updateEntity {
                helmet?.getActualItem(prop.animation)?.getItem()?.let {
                    equipment[EquipmentSlot.HEAD] = it
                }
                chestplate?.getActualItem(prop.animation)?.getItem()?.let {
                    equipment[EquipmentSlot.CHEST] = it
                }
                leggings?.getActualItem(prop.animation)?.getItem()?.let {
                    equipment[EquipmentSlot.LEGS] = it
                }
                boots?.getActualItem(prop.animation)?.getItem()?.let {
                    equipment[EquipmentSlot.FEET] = it
                }
            }
            return
        } else {
            entity.updateEntity {
                helmet?.getActualItem(prop.animation)?.getItem()?.let {
                    entityData += "item" to object : EntityData {
                        override val id: String
                            get() = "item"

                        override fun apply(entity: Entity) {
                            if (entity !is ItemDisplay) return
                            entity.setItemStack(it)
                        }
                    }
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