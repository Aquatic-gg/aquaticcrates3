package gg.aquatic.aquaticcrates.api.animation.crate

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import gg.aquatic.aquaticseries.lib.util.getSectionList
import gg.aquatic.waves.registry.serializer.ActionSerializer
import org.bukkit.configuration.ConfigurationSection
import java.util.*

abstract class AnimationSettingsFactory {

    abstract fun serialize(section: ConfigurationSection?): CrateAnimationSettings?

    protected fun loadFinalActions(section: ConfigurationSection): MutableList<ConfiguredAction<Animation>> {
        return ActionSerializer.fromSections<Animation>(section.getSectionList("final-tasks")).toMutableList()
    }

    protected fun loadAnimationTasks(section: ConfigurationSection?): TreeMap<Int, MutableList<ConfiguredAction<Animation>>> {
        val tasks = TreeMap<Int, MutableList<ConfiguredAction<Animation>>>()
        if (section == null) return tasks

        for (key in section.getKeys(false)) {
            val delay = key.toIntOrNull() ?: continue
            tasks[delay] =
                ActionSerializer.fromSections<Animation>(section.getSectionList(key)).toMutableList()
        }

        return tasks
    }
    protected fun loadAnimationLength(section: ConfigurationSection): Int {
        return section.getInt("length", 0)
    }
    protected fun loadPreAnimationDelay(section: ConfigurationSection): Int {
        return section.getInt("pre-animation.delay", 0)
    }
    protected fun loadPostAnimationDelay(section: ConfigurationSection): Int {
        return section.getInt("post-animation.delay", 0)
    }
    protected fun loadSkippable(section: ConfigurationSection): Boolean {
        return section.getBoolean("skippable", false)
    }
    protected fun loadIsPersonal(section: ConfigurationSection): Boolean {
        return section.getBoolean("personal", false)
    }
    protected fun loadPreAnimationTasks(section: ConfigurationSection): TreeMap<Int, MutableList<ConfiguredAction<Animation>>> {
        return loadAnimationTasks(section.getConfigurationSection("pre-animation.tasks"))
    }
    protected fun loadPostAnimationTasks(section: ConfigurationSection): TreeMap<Int, MutableList<ConfiguredAction<Animation>>> {
        return loadAnimationTasks(section.getConfigurationSection("post-animation.tasks"))
    }

}