package gg.aquatic.aquaticcrates.plugin.animation.pouch

import gg.aquatic.aquaticcrates.api.animation.pouch.PouchAnimation
import gg.aquatic.aquaticcrates.api.animation.pouch.PouchAnimationManager
import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import gg.aquatic.aquaticseries.lib.util.executeActions
import gg.aquatic.aquaticseries.lib.util.randomItem
import org.bukkit.entity.Player

class InstantPouchAnimationImpl(
    override val player: Player,
    override val animationManager: PouchAnimationManager,
) : PouchAnimation() {
    override var state: State = State.PRE_OPEN
        private set

    override var tick: Int = 0
        private set

    val randomReward = animationManager.pouch.getRandomRewards(player,1).values.first().first
    val randomAmount = randomReward.amountRanges.randomItem()?.randomNum ?: 1

    override fun tick() {
        finalize()
        /*
        when (state) {
            State.PRE_OPEN -> {
                tickPreOpen()
                if (tick >= animationManager.preAnimationDelay) {
                    updateState(State.OPENING)
                    tick()
                    return
                }
            }

            State.OPENING -> {
                tickOpening()
                if (tick >= animationManager.animationLength) {
                    updateState(State.POST_OPEN)
                    tick()
                    return
                }
            }

            State.POST_OPEN -> {
                tickPostOpen()
                if (tick >= animationManager.postAnimationDelay) {
                    finalize()
                    return
                }
            }

            else -> {
                return
            }
        }

        tick++
         */
    }

    private fun updateState(state: State) {
        this.state = state
        tick = 0
    }

    private fun finalize() {
        updateState(State.FINISHED)
        executeActions(animationManager.animationSettings.finalAnimationTasks)
        animationManager.playingAnimations.remove(player.uniqueId)
        randomReward.give(player, randomAmount)
    }

    override fun executeActions(actions: List<ConfiguredAction<PouchAnimation>>) {
        actions.executeActions(this) { _, str ->
            str.replace("%player%", player.name).replace("%random-amount%", randomAmount.toString())
        }
    }
}