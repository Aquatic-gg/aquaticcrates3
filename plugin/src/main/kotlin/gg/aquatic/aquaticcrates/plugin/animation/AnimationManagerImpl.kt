package gg.aquatic.aquaticcrates.plugin.animation

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationManager
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationSettings
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.reroll.RerollManager
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.HashMap

class AnimationManagerImpl(
    override val crate: OpenableCrate,
    override val animationSettings: CrateAnimationSettings,
    rerollManager: (OpenableCrate) -> RerollManager?,
) : CrateAnimationManager() {
    override val rerollManager = rerollManager(crate)

    override val playingAnimations: HashMap<UUID, MutableList<CrateAnimation>> = hashMapOf()


    override fun playAnimation(animation: CrateAnimation) {
        val animations = playingAnimations.getOrPut(animation.player.uniqueId) { ArrayList() }
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

    }

    override fun forceStopAnimation(player: Player) {

    }
}