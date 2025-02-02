package gg.aquatic.aquaticcrates.plugin.animation.open

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationManager
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.aquaticcrates.plugin.animation.prop.inventory.InventoryAnimationProp
import gg.aquatic.waves.util.audience.AquaticAudience
import gg.aquatic.waves.util.runSync
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
        val availableRerolls = rerollManager.availableRerolls(player)
        if (availableRerolls <= usedRerolls) {
            updateState(State.POST_OPEN)
            tick()
            return
        }
        val animationMenu = props["inventory"] as? InventoryAnimationProp
        animationMenu?.menu?.close()

        updateState(State.ROLLING)
        rerollManager.openReroll(player, this, rewards).thenAcceptAsync { result ->
            if (result.reroll) {
                updateState(State.OPENING)
                rewards.clear()
                for ((_, prop) in props) {
                    prop.onAnimationEnd()
                }
                props.clear()
                rewards += crate.rewardManager.getRewards(player)
                tick()
            } else {
                updateState(State.POST_OPEN)
                tick()
            }
        }.exceptionally {
            it.printStackTrace()
            null
        }
        usedRerolls++
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
        props.clear()
        runSync {
            for (reward in rewards) {
                reward.give(player, false)
            }
        }
        animationManager.playingAnimations[player.uniqueId]?.let {
            it.remove(this)
            if (it.isEmpty()) {
                animationManager.playingAnimations.remove(player.uniqueId)
            }
        }
        completionFuture.complete(null)

    }

    override fun skip() {
        if (state == State.ROLLING || state == State.FINISHED) return
        tryReroll()
    }
}