package gg.aquatic.aquaticcrates.plugin.animation

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationManager
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.waves.util.audience.AquaticAudience
import gg.aquatic.waves.util.executeActions
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
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

    var usedRerolls = 0
    private fun tryReroll() {
        val crate = animationManager.crate

        if (crate !is OpenableCrate) {
            updateState(State.POST_OPEN)
            tick()
            return
        }

        val rerollManager = animationManager.rerollManager
        if (rerollManager == null) {
            updateState(State.POST_OPEN)
            tick()
            return
        }
        var availableRerolls = 0
        for ((id, rerolls) in rerollManager.groups) {
            if (!player.hasPermission("aquaticcrates.reroll.$id")) continue
            if (rerolls > availableRerolls) {
                availableRerolls = rerolls
            }
        }
        if (availableRerolls <= usedRerolls) {
            updateState(State.POST_OPEN)
            tick()
            return
        }
        updateState(State.ROLLING)
        usedRerolls++
        rerollManager.openReroll(player, crate.rewardManager.getPossibleRewards(player).values).thenAccept { result ->
            if (result.reroll) {
                updateState(State.OPENING)
                rewards.clear()
                for ((_, prop) in props) {
                    prop.onAnimationEnd()
                }
                rewards += crate.rewardManager.getRewards(player)
                tick()
            } else {
                updateState(State.POST_OPEN)
                tick()
            }
        }
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

    override fun skip() {
        tryReroll()
    }
}