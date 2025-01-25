package gg.aquatic.aquaticcrates.plugin.animation.open

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationManager
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationSettings
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.reroll.RerollManager
import gg.aquatic.waves.util.runAsync
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class AnimationManagerImpl(
    override val crate: OpenableCrate,
    override val animationSettings: CrateAnimationSettings,
    rerollManager: (OpenableCrate) -> RerollManager?,
) : CrateAnimationManager() {
    override val rerollManager = rerollManager(crate)

    override val playingAnimations: ConcurrentHashMap<UUID, MutableSet<CrateAnimation>> = ConcurrentHashMap()



    override fun playAnimation(animation: CrateAnimation) {
        val animations = playingAnimations.getOrPut(animation.player.uniqueId) { ConcurrentHashMap.newKeySet() }
        animations += animation
    }

    override fun tick() {
        for ((_, animations) in playingAnimations) {
            for (animation in animations) {
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
        playingAnimations.remove(player.uniqueId)?.forEach {
            for (reward in it.rewards) {
                reward.give(player, false)
            }
            runAsync {
                for (value in it.props.values) {
                    value.onAnimationEnd()
                }
            }
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
}