package gg.aquatic.aquaticcrates.api.animation.crate

import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.event.CrateAnimationEndEvent
import gg.aquatic.aquaticcrates.api.event.CrateAnimationStartEvent
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.aquaticcrates.api.runOrCatch
import gg.aquatic.waves.api.event.call
import gg.aquatic.waves.scenario.PlayerScenario
import gg.aquatic.waves.scenario.ScenarioProp
import gg.aquatic.waves.util.collection.executeActions
import gg.aquatic.waves.util.decimals
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import gg.aquatic.waves.util.task.AsyncCtx
import gg.aquatic.waves.util.task.BukkitCtx
import gg.aquatic.waves.util.updatePAPIPlaceholders
import kotlinx.coroutines.launch
import net.kyori.adventure.key.Key
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

abstract class CrateAnimation : PlayerScenario() {

    abstract val animationManager: CrateAnimationManager

    abstract val rewards: MutableList<RolledReward>

    override val extraPlaceholders: MutableMap<Key, (String) -> String> = ConcurrentHashMap()
    override val props: MutableMap<Key, ScenarioProp> = ConcurrentHashMap()

    fun tickPreOpen() {
        executeActions(settings.preAnimationTasks[tick] ?: return)
    }

    fun tickOpening() {
        executeActions(settings.animationTasks[tick] ?: return)
    }

    fun tickPostOpen() {
        executeActions(settings.postAnimationTasks[tick] ?: return)
    }

    open fun executeActions(actions: Collection<ConfiguredExecutableObject<CrateAnimation, Unit>>) {
        for (a in actions) {
            runOrCatch {
                a.execute(this) { a, str -> a.updatePlaceholders(str) }
            }
        }
    }

    var phase: Phase = PreOpenPhase()

    override fun onTick() {
        phase.tick()
    }

    abstract val completionFuture: CompletableFuture<CrateAnimation>
    abstract val settings: CrateAnimationSettings

    val playerEquipment = ConcurrentHashMap<EquipmentSlot, ItemStack>()

    inner class PreOpenPhase : Phase {
        override fun tick() {
            tickPreOpen()
            if (tick >= settings.preAnimationDelay) {
                val event =
                    CrateAnimationStartEvent(animationManager.crate as OpenableCrate, this@CrateAnimation, player)
                if (Bukkit.isPrimaryThread()) {
                    AsyncCtx {
                        event.call()
                    }
                } else {
                    event.call()
                }
                updatePhase(OpeningPhase())
                return
            }
            tickProps()
        }
    }

    inner class OpeningPhase : Phase {
        override fun tick() {
            tickOpening()
            if (tick >= settings.animationLength) {
                tryReroll()
                return
            }
            tickProps()
        }
    }

    inner class PostOpenPhase : Phase {
        override fun tick() {
            tickPostOpen()
            if (tick >= settings.postAnimationDelay) {
                finalizeAnimation()
                return
            }
            tickProps()
        }
    }

    inner class RollingPhase : Phase {
        override fun tick() {

        }
    }

    inner class FinalPhase : Phase {
        override fun tick() {

        }
    }

    fun tryReroll() {
        val crate = animationManager.crate

        if (crate !is OpenableCrate) {
            updatePhase(PostOpenPhase())
            return
        }

        val rerollManager = animationManager.rerollManager
        if (rerollManager == null) {
            updatePhase(PostOpenPhase())
            return
        }
        val availableRerolls = rerollManager.availableRerolls(player)
        if (availableRerolls <= usedRerolls) {
            updatePhase(PostOpenPhase())
            return
        }
        updatePhase(RollingPhase())
        rerollManager.animationTasks.executeActions(this) { a, str -> a.updatePlaceholders(str) }
        onReroll()
    }

    abstract fun onReroll()

    fun finalizeAnimation() {
        updatePhase(FinalPhase())

        val eventRunnable = {
            CrateAnimationEndEvent(animationManager.crate as OpenableCrate, this, player).call()
        }
        if (Bukkit.isPrimaryThread()) {
            AsyncCtx {
                eventRunnable()
            }
        } else {
            eventRunnable()
        }

        runOrCatch {
            onFinalize()
        }
        executeActions(settings.finalAnimationTasks)
        destroy()
        val block = {
            for (reward in rewards) {
                runOrCatch {
                    reward.give(player)
                }
            }
        }

        if (Bukkit.isPrimaryThread()) {
            block()
        } else {
            BukkitCtx {
                block()
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

    open fun onFinalize() {}

    fun updatePhase(phase: Phase) {
        onPhaseUpdate(phase)
        this.phase = phase
        tick = 0
        phase.tick()
    }

    open fun onPhaseUpdate(phase: Phase) {}

    override fun updatePlaceholders(original: String): String {
        var finalString = original.replace("%player%", player.name)

        for ((i, reward) in rewards.withIndex()) {
            finalString = finalString
                .replace("%random-amount:$i%", reward.randomAmount.toString())
                .replace("%reward-name:$i%", reward.reward.displayName)
                .replace("%reward-rarity-name:$i%", reward.reward.rarity.displayName)
                .replace("%reward-rarity-id:$i%", reward.reward.rarity.rarityId)
                .replace("%reward-id:$i%", reward.reward.id)
                .replace("%reward-chance:$i%", (reward.reward.chance * 100.0).decimals(2))
            reward.reward.variables.forEach { (key, value) ->
                finalString = finalString.replace("%reward-var:$i:$key:$i%", value)
            }
        }
        val available = animationManager.rerollManager?.availableRerolls(player) ?: 0
        finalString = finalString
            .replace("%rerolls-total%", available.toString())
            .replace("%rerolls-used%", usedRerolls.toString())
            .replace("%rerolls-remaining%", (available - usedRerolls).toString())

        for ((_, extraPlaceholder) in extraPlaceholders) {
            finalString = extraPlaceholder(finalString)
        }

        return finalString.updatePAPIPlaceholders(player)
    }

    var usedRerolls = 0

    fun skip() {
        if (phase is RollingPhase || phase is FinalPhase) return
        tryReroll()
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