package gg.aquatic.aquaticcrates.plugin.animation

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationManager
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import gg.aquatic.aquaticseries.lib.audience.AquaticAudience
import gg.aquatic.aquaticseries.lib.util.executeActions
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

class RegularAnimationImpl(
    override val player: Player,
    override val animationManager: CrateAnimationManager,
    override val baseLocation: Location,
    override val rewards: MutableList<RolledReward>,
    override val audience: AquaticAudience,
    val completionFuture: CompletableFuture<Void>
) : CrateAnimation() {
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
        for (reward in rewards) {
            reward.give(player, false)
        }
        completionFuture.complete(null)
    }

    override fun executeActions(actions: List<ConfiguredAction<Animation>>) {
        actions.executeActions(this) { _, str ->
            var finalString = updatePlaceholders(str)
            finalString
        }
    }
}