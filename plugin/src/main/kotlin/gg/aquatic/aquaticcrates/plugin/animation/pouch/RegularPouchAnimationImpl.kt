package gg.aquatic.aquaticcrates.plugin.animation.pouch

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.pouch.PouchAnimation
import gg.aquatic.aquaticcrates.api.animation.pouch.PouchAnimationManager
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import gg.aquatic.aquaticseries.lib.audience.AquaticAudience
import gg.aquatic.aquaticseries.lib.util.executeActions
import org.bukkit.Location
import org.bukkit.entity.Player

class RegularPouchAnimationImpl(
    override val player: Player,
    override val animationManager: PouchAnimationManager,
    override val baseLocation: Location,
    override val rewards: MutableList<RolledReward>,
    override val audience: AquaticAudience
) : PouchAnimation() {
    override var state: State = State.PRE_OPEN
        private set


    private val settings = animationManager.animationSettings

    override val props: MutableMap<String, AnimationProp> = hashMapOf()


    override fun tick() {
        when (state) {
            State.PRE_OPEN -> {
                tickPreOpen()
                if (tick >= settings.preAnimationDelay) {
                    updateState(State.OPENING)
                    tick()
                    return
                }
            }

            State.OPENING -> {
                tickOpening()
                if (tick >= settings.animationLength) {
                    updateState(State.POST_OPEN)
                    tick()
                    return
                }
            }

            State.POST_OPEN -> {
                tickPostOpen()
                if (tick >= settings.postAnimationDelay) {
                    finalize()
                    return
                }
            }

            else -> {
                return
            }
        }
        for ((_, prop) in props) {
            prop.tick()
        }

        tick++
    }

    private fun updateState(state: State) {
        this.state = state
        tick = 0
    }

    private fun finalize() {
        updateState(State.FINISHED)
        executeActions(animationManager.animationSettings.finalAnimationTasks)
        for ((_, prop) in props) {
            prop.onAnimationEnd()
        }
        player.sendMessage("Finalizing animation")
        animationManager.playingAnimations.remove(player.uniqueId)
        for (reward in rewards) {
            reward.give(player)
        }
        //.give(player, randomAmount)
    }

    override fun executeActions(actions: List<ConfiguredAction<Animation>>) {
        actions.executeActions(this) { _, str ->
            var finalString = str.replace("%player%", player.name)
            for ((i, reward) in rewards.withIndex()) {
                finalString = finalString.replace("%random-amount:$i",reward.randomAmount.toString())
            }
            finalString
        }
    }
}