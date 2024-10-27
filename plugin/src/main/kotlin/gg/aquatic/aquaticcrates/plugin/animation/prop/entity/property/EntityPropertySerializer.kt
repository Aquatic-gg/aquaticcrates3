package gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property

import org.bukkit.configuration.ConfigurationSection

interface EntityPropertySerializer {

    suspend fun load(section: ConfigurationSection): EntityProperty

}