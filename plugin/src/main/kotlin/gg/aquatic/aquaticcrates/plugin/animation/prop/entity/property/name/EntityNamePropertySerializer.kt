package gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.name

import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityPropertySerializer
import gg.aquatic.aquaticseries.lib.util.toAquatic
import org.bukkit.configuration.ConfigurationSection

object EntityNamePropertySerializer: EntityPropertySerializer {
    override fun load(section: ConfigurationSection): EntityProperty {
        return EntityNameProperty(section.getString("display-name","")!!.toAquatic())
    }
}