package gg.aquatic.aquaticcrates.plugin.animation

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationManager
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.waves.util.audience.AquaticAudience
import gg.aquatic.waves.util.executeActions
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

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
    override val props: ConcurrentHashMap<String, AnimationProp> = ConcurrentHashMap()


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

        Bukkit.broadcastMessage("Tick: $tick")
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

    override fun executeActions(actions: List<ConfiguredExecutableObject<Animation,Unit>>) {
        actions.executeActions(this) { _, str ->
            var finalString = updatePlaceholders(str)
            finalString
        }
    }
}