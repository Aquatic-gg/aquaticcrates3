package gg.aquatic.aquaticcrates.api.animation.crate

import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.waves.util.updatePAPIPlaceholders

abstract class CrateAnimation : PlayerBoundAnimation() {

    abstract val animationManager: CrateAnimationManager

    abstract val state: State

    abstract val rewards: MutableList<RolledReward>

    fun tickPreOpen() {
        executeActions(animationManager.animationSettings.preAnimationTasks[tick] ?: return)
    }

    fun tickOpening() {
        executeActions(animationManager.animationSettings.animationTasks[tick] ?: return)
    }

    fun tickPostOpen() {
        executeActions(animationManager.animationSettings.postAnimationTasks[tick] ?: return)
    }

    open fun executeActions(actions: CrateAnimationActions) {
        actions.execute(this)
    }

    override fun updatePlaceholders(str: String): String {
        var finalString = str.replace("%player%", player.name).updatePAPIPlaceholders(player)

        for ((i, reward) in rewards.withIndex()) {
            finalString = finalString
                .replace("%random-amount:$i%", reward.randomAmount.toString())
                .replace("%reward-name:$i%", reward.reward.displayName)
                .replace("%reward-id:$i%", reward.reward.id)
                .replace("%reward-chance:$i%", reward.reward.chance.toString())
        }
        val available = animationManager.rerollManager?.availableRerolls(player) ?: 0
        finalString = finalString
            .replace("%rerolls-total%", available.toString())
            .replace("%rerolls-used%", usedRerolls.toString())
            .replace("%rerolls-remaining%", (available - usedRerolls).toString())

        for ((_, extraPlaceholder) in extraPlaceholders) {
            finalString = extraPlaceholder(finalString)
        }

        return finalString
    }

    var usedRerolls = 0

    abstract fun skip()

    enum class State {
        PRE_OPEN,
        OPENING,
        ROLLING,
        POST_OPEN,
        FINISHED,
    }

}