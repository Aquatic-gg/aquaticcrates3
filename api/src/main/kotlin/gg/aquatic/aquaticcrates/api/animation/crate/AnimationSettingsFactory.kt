package gg.aquatic.aquaticcrates.api.animation.crate

import gg.aquatic.waves.registry.serializer.ActionSerializer
import gg.aquatic.waves.util.generic.ClassTransform
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import gg.aquatic.waves.util.getSectionList
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import java.util.*

abstract class AnimationSettingsFactory {

    abstract fun serialize(section: ConfigurationSection?): CrateAnimationSettings?

    protected fun loadFinalActions(section: ConfigurationSection): Collection<ConfiguredExecutableObject<CrateAnimation, Unit>> {
        val animationTasks = ActionSerializer.fromSections<CrateAnimation>(section.getSectionList("final-tasks"),
            ClassTransform(Player::class.java) { a -> a.player }).toMutableList()

        return animationTasks
    }

    protected fun loadAnimationTasks(section: ConfigurationSection?, duration: Int): TreeMap<Int, Collection<ConfiguredExecutableObject<CrateAnimation, Unit>>> {
        val tasks = TreeMap<Int, Collection<ConfiguredExecutableObject<CrateAnimation, Unit>>>()
        if (section == null) return tasks

        for (key in section.getKeys(false)) {
            val actions = ActionSerializer.fromSections<CrateAnimation>(section.getSectionList(key),
                ClassTransform(Player::class.java) { a -> a.player }).toMutableList()

            if (key.lowercase().startsWith("every-")) {
                val every = key.substringAfter("every-").toInt()
                var currentI = 0
                while (true) {
                    if (currentI >= duration) {
                        break
                    }
                    val list = tasks.getOrPut(currentI) { mutableListOf() } as MutableList
                    list += actions
                    currentI += every
                }
                continue
            } else if (key.contains(";")) {
                val split = key.split(";").map { it.toInt() }
                for (i in split) {
                    val list = tasks.getOrPut(i) { mutableListOf() } as MutableList
                    list += actions
                }
                continue
            }

            val list = tasks.getOrPut(key.toIntOrNull() ?: continue) { mutableListOf() } as MutableList
            list += actions
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

    protected fun loadPreAnimationTasks(section: ConfigurationSection, duration: Int): TreeMap<Int, Collection<ConfiguredExecutableObject<CrateAnimation, Unit>>> {
        return loadAnimationTasks(section.getConfigurationSection("pre-animation.tasks"),duration)
    }

    protected fun loadPostAnimationTasks(section: ConfigurationSection, duration: Int): TreeMap<Int, Collection<ConfiguredExecutableObject<CrateAnimation, Unit>>> {
        return loadAnimationTasks(section.getConfigurationSection("post-animation.tasks"),duration)
    }

}