package gg.aquatic.aquaticcrates.api.animation.pouch

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import gg.aquatic.aquaticseries.lib.util.executeActions

abstract class PouchAnimation: Animation() {

    abstract val animationManager: PouchAnimationManager

    abstract val state: State

    enum class State {
        PRE_OPEN,
        OPENING,
        POST_OPEN,
        FINISHED,
    }

    override fun tickPreOpen() {
        executeActions(animationManager.animationSettings.preAnimationTasks[tick] ?: return)
    }

    override fun tickOpening() {
        executeActions(animationManager.animationSettings.animationTasks[tick] ?: return)
    }

    override fun tickPostOpen() {
        executeActions(animationManager.animationSettings.postAnimationTasks[tick] ?: return)
    }


    open fun executeActions(actions: List<ConfiguredAction<Animation>>) {
        actions.executeActions(this) { _, str -> updatePlaceholders(str) }
    }
}