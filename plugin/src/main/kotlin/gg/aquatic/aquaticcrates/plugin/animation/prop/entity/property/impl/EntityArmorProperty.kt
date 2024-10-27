package gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl

import gg.aquatic.aquaticcrates.api.util.animationitem.ArgumentItem
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.EntityAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityPropertySerializer
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Entity
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.LivingEntity

class EntityArmorProperty(
    val helmet: ArgumentItem?,
    val chestplate: ArgumentItem?,
    val leggings: ArgumentItem?,
    val boots: ArgumentItem?,
): EntityProperty {
    override fun apply(entity: Entity, prop: EntityAnimationProp) {
        val living = entity as? LivingEntity
        if (living != null) {
            if (helmet != null) {
                living.equipment?.helmet = helmet.getActualItem(prop.animation).getItem()
            }
            if (chestplate != null) {
                living.equipment?.chestplate = chestplate.getActualItem(prop.animation).getItem()
            }
            if (leggings != null) {
                living.equipment?.leggings = leggings.getActualItem(prop.animation).getItem()
            }
            if (boots != null) {
                living.equipment?.boots = boots.getActualItem(prop.animation).getItem()
            }
            return
        }
        if (entity is ItemDisplay) {
            entity.itemStack = helmet?.getActualItem(prop.animation)?.getItem() ?: return
            return
        }
    }

    object Serializer : EntityPropertySerializer {
        override suspend fun load(section: ConfigurationSection): EntityProperty {
            val helmet = ArgumentItem.loadFromYml(section.getConfigurationSection("armor.helmet"))
            val chestplate = ArgumentItem.loadFromYml(section.getConfigurationSection("armor.chestplate"))
            val leggings = ArgumentItem.loadFromYml(section.getConfigurationSection("armor.leggings"))
            val boots = ArgumentItem.loadFromYml(section.getConfigurationSection("armor.boots"))
            return EntityArmorProperty(helmet, chestplate, leggings, boots)
        }

    }
}