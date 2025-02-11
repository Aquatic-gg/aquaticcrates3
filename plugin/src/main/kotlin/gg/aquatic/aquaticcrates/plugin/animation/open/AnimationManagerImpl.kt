package gg.aquatic.aquaticcrates.plugin.animation.open

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationManager
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationSettings
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate
import gg.aquatic.aquaticcrates.api.reroll.RerollManager
import gg.aquatic.aquaticcrates.plugin.animation.idle.settings.IdleAnimationSettings
import gg.aquatic.waves.util.chance.randomItem
import gg.aquatic.waves.util.runAsync
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class AnimationManagerImpl(
    override val crate: OpenableCrate,
    override val animationSettings: CrateAnimationSettings,
    val idleAnimationSettings: Collection<IdleAnimationSettings>,
    rerollManager: (OpenableCrate) -> RerollManager?,
) : CrateAnimationManager() {
    override val rerollManager = rerollManager(crate)

    override val playingAnimations: ConcurrentHashMap<UUID, MutableSet<CrateAnimation>> = ConcurrentHashMap()
    override var idleAnimation: ConcurrentHashMap<SpawnedCrate, Animation> = ConcurrentHashMap()

    override val failAnimations: ConcurrentHashMap<SpawnedCrate, ConcurrentHashMap<UUID, out PlayerBoundAnimation>> =
        ConcurrentHashMap()

    override fun playNewIdleAnimation(spawnedCrate: SpawnedCrate) {
        val animation = idleAnimationSettings.randomItem() ?: return
        idleAnimation[spawnedCrate] = animation.create(spawnedCrate)
    }

    override fun playAnimation(animation: CrateAnimation) {
        //Bukkit.broadcastMessage("\n Playing animation \n")
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
                        value.onAnimationEnd()
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
            animation.props.values.forEach { it.onAnimationEnd() }
        }
    }

    override fun forceStopAllAnimations() {
        forceStopAnimations()

        for ((_, map) in failAnimations) {
            for ((_, animation) in map) {
                animation.props.values.forEach { it.onAnimationEnd() }
            }
        }
        playingAnimations.clear()

        failAnimations.clear()
        for ((_, animation) in idleAnimation) {
            animation.props.values.forEach { it.onAnimationEnd() }
        }
        idleAnimation.clear()
    }
}