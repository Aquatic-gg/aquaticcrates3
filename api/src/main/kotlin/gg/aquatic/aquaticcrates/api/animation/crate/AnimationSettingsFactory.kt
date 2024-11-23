package gg.aquatic.aquaticcrates.api.animation.crate

import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import gg.aquatic.aquaticseries.lib.util.getSectionList
import gg.aquatic.waves.registry.serializer.ActionSerializer
import org.bukkit.configuration.ConfigurationSection

abstract class AnimationSettingsFactory {

    abstract fun serialize(section: ConfigurationSection?): CrateAnimationSettings?

    protected fun loadFinalActions(section: ConfigurationSection): MutableList<ConfiguredAction<CrateAnimation>> {
        return ActionSerializer.fromSections<CrateAnimation>(section.getSectionList("final-tasks")).toMutableList()
    }

}