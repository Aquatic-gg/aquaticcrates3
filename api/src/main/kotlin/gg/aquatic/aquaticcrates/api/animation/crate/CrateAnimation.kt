package gg.aquatic.aquaticcrates.api.animation.crate

import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.waves.util.decimals
import gg.aquatic.waves.util.runSync
import gg.aquatic.waves.util.updatePAPIPlaceholders
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

abstract class CrateAnimation : PlayerBoundAnimation() {

    abstract val animationManager: CrateAnimationManager

    abstract var state: State
        protected set

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

    abstract val completionFuture: CompletableFuture<CrateAnimation>
    abstract val settings: CrateAnimationSettings

    val playerEquipment = ConcurrentHashMap<EquipmentSlot, ItemStack>()

    override fun tick() {
        try {
            onTick()
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
                        finalizeAnimation()
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    open fun onTick() {}

    fun tryReroll() {
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
        updateState(State.ROLLING)
        onReroll()
    }

    abstract fun onReroll()

    fun finalizeAnimation(isSync: Boolean = false) {
        updateState(State.FINISHED)
        onFinalize(isSync)
        executeActions(animationManager.animationSettings.finalAnimationTasks)
        for ((_, prop) in props) {
            prop.onAnimationEnd()
        }
        props.clear()
        if (isSync) {
            for (reward in rewards) {
                reward.give(player, false)
            }
        } else {
            runSync {
                for (reward in rewards) {
                    reward.give(player, false)
                }
            }
        }

        animationManager.playingAnimations[player.uniqueId]?.let {
            it.remove(this)
            if (it.isEmpty()) {
                animationManager.playingAnimations.remove(player.uniqueId)
            }
        }
        completionFuture.complete(this)
    }

    open fun onFinalize(isSync: Boolean) {}

    fun updateState(state: State) {
        onStateUpdate(state)
        this.state = state
        tick = 0
    }

    open fun onStateUpdate(state: State) {}

    override fun updatePlaceholders(str: String): String {
        var finalString = str.replace("%player%", player.name).updatePAPIPlaceholders(player)

        for ((i, reward) in rewards.withIndex()) {
            finalString = finalString
                .replace("%random-amount:$i%", reward.randomAmount.toString())
                .replace("%reward-name:$i%", reward.reward.displayName)
                .replace("%reward-rarity-name:$i%", reward.reward.rarity.displayName)
                .replace("%reward-rarity-id:$i%", reward.reward.rarity.rarityId)
                .replace("%reward-id:$i%", reward.reward.id)
                .replace("%reward-chance:$i%", (reward.reward.chance * 100.0).decimals(2))
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

    fun skip() {
        if (state == State.ROLLING || state == State.FINISHED) return
        tryReroll()
    }

    enum class State {
        PRE_OPEN,
        OPENING,
        ROLLING,
        POST_OPEN,
        FINISHED,
    }

    enum class EquipmentSlot {
        HELMET, CHESTPLATE, LEGGINGS, BOOTS, HAND, OFFHAND, NUM_0, NUM_1, NUM_2, NUM_3, NUM_4, NUM_5, NUM_6, NUM_7, NUM_8;

        fun toSlot(player: Player): Int {
            return when (this) {
                HELMET -> 5
                CHESTPLATE -> 6
                LEGGINGS -> 7
                BOOTS -> 8
                HAND -> {
                    player.inventory.heldItemSlot
                }
                OFFHAND -> 45
                NUM_0 -> 36
                NUM_1 -> 37
                NUM_2 -> 38
                NUM_3 -> 39
                NUM_4 -> 40
                NUM_5 -> 41
                NUM_6 -> 42
                NUM_7 -> 43
                NUM_8 -> 44
            }
        }
    }

}