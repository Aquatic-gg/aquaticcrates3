package gg.aquatic.aquaticcrates.api.animation.pouch

import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import gg.aquatic.aquaticseries.lib.util.executeActions
import org.bukkit.entity.Player

abstract class PouchAnimation {

    abstract val player: Player
    abstract val animationManager: PouchAnimationManager

    abstract val state: State

    abstract val tick: Int
    abstract fun tick()

    enum class State {
        PRE_OPEN,
        OPENING,
        POST_OPEN,
        FINISHED,
    }

    open fun tickPreOpen() {
        executeActions(animationManager.preAnimationTasks[tick] ?: return)
    }

    open fun tickOpening() {
        executeActions(animationManager.animationTasks[tick] ?: return)
    }

    open fun tickPostOpen() {
        executeActions(animationManager.postAnimationTasks[tick] ?: return)
    }


    open fun executeActions(actions: List<ConfiguredAction<PouchAnimation>>) {
        actions.executeActions(this) { _, str -> str.replace("%player%", player.name) }
    }
}