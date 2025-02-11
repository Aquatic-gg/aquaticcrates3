package gg.aquatic.aquaticcrates.api.animation.crate

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.waves.registry.serializer.ActionSerializer
import gg.aquatic.waves.util.getSectionList
import org.bukkit.configuration.ConfigurationSection
import java.util.*

abstract class AnimationSettingsFactory {

    abstract fun serialize(section: ConfigurationSection?): CrateAnimationSettings?

    protected fun loadFinalActions(section: ConfigurationSection): CrateAnimationActions {
        val animationActions =
            ActionSerializer.fromSections<Animation>(section.getSectionList("final-tasks")).toMutableList()
        val playerBoundActions =
            ActionSerializer.fromSections<PlayerBoundAnimation>(section.getSectionList("final-tasks")).toMutableList()

        return CrateAnimationActions(
            animationActions,
            playerBoundActions,
        )
    }

    protected fun loadAnimationTasks(section: ConfigurationSection?): TreeMap<Int, CrateAnimationActions> {
        val tasks = TreeMap<Int, CrateAnimationActions>()
        if (section == null) return tasks

        for (key in section.getKeys(false)) {
            val delay = key.toIntOrNull() ?: continue

            val animationTasks = ActionSerializer.fromSections<Animation>(section.getSectionList(key)).toMutableList()
            val playerBoundTasks =
                ActionSerializer.fromSections<PlayerBoundAnimation>(section.getSectionList(key)).toMutableList()

            tasks[delay] = CrateAnimationActions(
                animationTasks,
                playerBoundTasks,
            )
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

    protected fun loadPreAnimationTasks(section: ConfigurationSection): TreeMap<Int, CrateAnimationActions> {
        return loadAnimationTasks(section.getConfigurationSection("pre-animation.tasks"))
    }

    protected fun loadPostAnimationTasks(section: ConfigurationSection): TreeMap<Int, CrateAnimationActions> {
        return loadAnimationTasks(section.getConfigurationSection("post-animation.tasks"))
    }

}