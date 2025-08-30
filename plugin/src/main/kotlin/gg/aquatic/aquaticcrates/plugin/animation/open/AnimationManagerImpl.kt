package gg.aquatic.aquaticcrates.plugin.animation.open

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationManager
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationSettings
import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate
import gg.aquatic.aquaticcrates.api.reroll.RerollManager
import gg.aquatic.aquaticcrates.plugin.animation.fail.FailAnimationSettings
import gg.aquatic.aquaticcrates.plugin.animation.idle.IdleAnimationSettings
import gg.aquatic.waves.scenario.PlayerScenario
import gg.aquatic.waves.scenario.Scenario
import gg.aquatic.waves.util.chance.randomItem
import gg.aquatic.waves.util.runAsync
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class AnimationManagerImpl(
    override val crate: OpenableCrate,
    override val animationSettings: CrateAnimationSettings,
    private val idleAnimationSettings: Collection<IdleAnimationSettings>,
    private val failAnimationSettings: FailAnimationSettings?,
    rerollManager: (OpenableCrate) -> RerollManager?,
) : CrateAnimationManager() {
    override val rerollManager = rerollManager(crate)

    override val playingAnimations: ConcurrentHashMap<UUID, MutableSet<CrateAnimation>> = ConcurrentHashMap()
    override var idleAnimation: ConcurrentHashMap<SpawnedCrate, Scenario> = ConcurrentHashMap()

    override val failAnimations: ConcurrentHashMap<SpawnedCrate, ConcurrentHashMap<UUID, PlayerScenario>> =
        ConcurrentHashMap()

    override fun playNewIdleAnimation(spawnedCrate: SpawnedCrate) {
        val animation = idleAnimationSettings.randomItem() ?: return
        idleAnimation[spawnedCrate] = animation.create(spawnedCrate)
    }

    override fun playFailAnimation(spawnedCrate: SpawnedCrate, player: Player) {
        val fail = failAnimations.getOrPut(spawnedCrate) { ConcurrentHashMap() }
        val previousAnimation = fail.remove(player.uniqueId)
        previousAnimation?.props?.values?.forEach { it.onEnd() }

        val new = failAnimationSettings?.create(spawnedCrate, player) ?: return
        fail[player.uniqueId] = new
    }

    override fun playAnimation(animation: CrateAnimation) {
        val spawnedCrate = CrateHandler.spawned[animation.baseLocation]
        if (spawnedCrate != null) {
            val fail = failAnimations[spawnedCrate]?.remove(animation.player.uniqueId)
            fail?.props?.values?.forEach { it.onEnd() }
        }
        val animations = playingAnimations.getOrPut(animation.player.uniqueId) { ConcurrentHashMap.newKeySet() }
        animations += animation
    }

    override fun tick() {
        for ((_, animations) in playingAnimations) {
            for (animation in animations.toMutableList()) {
                animation.tick()
            }
        }
        for ((_, animation) in idleAnimation) {
            animation.tick()
        }
        for (entry in failAnimations) {
            for ((_, animation) in entry.value.toMutableMap()) {
                animation.tick()
            }
        }
    }

    override fun skipAnimation(player: Player) {
        runAsync {
            playingAnimations[player.uniqueId]?.forEach { it.skip() }
        }
    }

    override fun forceStopAnimation(player: Player) {
        val animations = playingAnimations[player.uniqueId] ?: return
        for (animation in animations) {
            animation.finalizeAnimation(true)
        }
    }

    override fun forceStopAnimations() {
        for ((_, animations) in playingAnimations) {
            for (animation in animations) {
                animation.rewards.forEach { reward -> reward.give(animation.player, false) }
                runAsync {
                    for (value in animation.props.values) {
                        value.onEnd()
                    }
                }
            }
        }
        playingAnimations.clear()
    }

    override fun forceStopAllAnimationTypes(player: Player) {
        forceStopAnimation(player)
        for ((_, map) in failAnimations) {
            val animation = map.remove(player.uniqueId) ?: continue
            animation.props.values.forEach { it.onEnd() }
        }
    }

    override fun forceStopAllAnimations() {
        forceStopAnimations()

        for ((_, map) in failAnimations) {
            for ((_, animation) in map) {
                animation.props.values.forEach { it.onEnd() }
            }
        }
        playingAnimations.clear()

        failAnimations.clear()
        for ((_, animation) in idleAnimation) {
            animation.props.values.forEach { it.onEnd() }
        }
        idleAnimation.clear()
    }
}