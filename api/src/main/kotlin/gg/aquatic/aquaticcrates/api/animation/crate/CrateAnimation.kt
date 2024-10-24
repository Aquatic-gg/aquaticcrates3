package gg.aquatic.aquaticcrates.api.animation.crate

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.pouch.PouchAnimation
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import gg.aquatic.aquaticseries.lib.interactable2.SpawnedInteractable
import gg.aquatic.aquaticseries.lib.util.executeActions
import org.bukkit.entity.Player
import org.bukkit.util.Vector

abstract class CrateAnimation: Animation() {

    abstract val animationManager: CrateAnimationManager

    abstract val state: State


    override fun tickPreOpen() {
        executeActions(animationManager.animationSettings.preAnimationTasks[tick] ?: return)
    }

    override fun tickOpening() {
        executeActions(animationManager.animationSettings.animationTasks[tick] ?: return)
    }

    override fun tickPostOpen() {
        executeActions(animationManager.animationSettings.postAnimationTasks[tick] ?: return)
    }

    open fun executeActions(actions: List<ConfiguredAction<CrateAnimation>>) {
        actions.executeActions(this) { _, str ->
            var finalString = str.replace("%player%", player.name)
            for ((i, reward) in rewards.withIndex()) {
                finalString = finalString.replace("%random-amount:$i",reward.randomAmount.toString())
            }
            finalString
        }
    }

    enum class State {
        PRE_OPEN,
        OPENING,
        ROLLING,
        POST_OPEN,
        FINISHED,
    }

}