package gg.aquatic.aquaticcrates.plugin.animation.open

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationManager
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.aquaticcrates.plugin.animation.open.settings.CinematicAnimationSettings
import gg.aquatic.aquaticcrates.plugin.animation.prop.CameraAnimationProp
import gg.aquatic.waves.util.audience.AquaticAudience
import gg.aquatic.waves.util.runSync
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

class CinematicAnimationImpl(
    override val player: Player,
    override val animationManager: CrateAnimationManager,
    override val baseLocation: Location,
    override val rewards: MutableList<RolledReward>,
    override val audience: AquaticAudience,
    override val completionFuture: CompletableFuture<Void>,
    //val camera: CameraAnimationProp
) : CrateAnimation() {

    override var state: State = State.PRE_OPEN

    override val settings = animationManager.animationSettings as CinematicAnimationSettings

    private var attached = false
    override val props: ConcurrentHashMap<String, AnimationProp> = ConcurrentHashMap()

    /*
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
                    tryReroll()
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
     */

    override fun onReroll() {
        val crate = animationManager.crate as OpenableCrate
        val rerollManager = animationManager.rerollManager!!
        rerollManager.openReroll(player, this, rewards).thenAccept { result ->
            if (result.reroll) {
                updateState(State.OPENING)
                rewards.clear()

                for ((_, prop) in props) {
                    if (prop is CameraAnimationProp) {
                        prop.boundPaths.clear()
                        continue
                    }
                    prop.onAnimationEnd()
                }
                props.toList().forEach { if (it.first.lowercase() != "camera") props.remove(it.first) }
                rewards += crate.rewardManager.getRewards(player)
                tick()
            } else {
                updateState(State.POST_OPEN)
                tick()
            }
        }
        usedRerolls++
    }

    override fun onStateUpdate(state: State) {
        if (state == State.OPENING && !attached) {
            attached = true
            val cameraProp = props["camera"] as CameraAnimationProp
            cameraProp.attachPlayer()
        }
    }
}