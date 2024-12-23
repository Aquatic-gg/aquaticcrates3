package gg.aquatic.aquaticcrates.api.animation.crate

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.waves.util.executeActions
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import gg.aquatic.waves.util.updatePAPIPlaceholders

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

    open fun executeActions(actions: List<ConfiguredExecutableObject<Animation,Unit>>) {
        actions.executeActions(this) { _, str ->
            var finalString = str.replace("%player%", player.name)
            for ((i, reward) in rewards.withIndex()) {
                finalString = finalString.replace("%random-amount:$i",reward.randomAmount.toString())
            }
            finalString.updatePAPIPlaceholders(player)
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