package gg.aquatic.aquaticcrates.api.animation.crate

import gg.aquatic.aquaticcrates.api.animation.AnimationTitle
import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.reroll.RerollManager
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

abstract class CrateAnimationManager {

    abstract val crate: Crate
    abstract val animationSettings: CrateAnimationSettings
    abstract val rerollManager: RerollManager?

    open fun showAnimationTitle(title: AnimationTitle, player: Player) {

    }

    open fun hideAnimationTitle(title: AnimationTitle, player: Player) {

    }

    abstract val playingAnimations: ConcurrentHashMap<UUID, MutableSet<CrateAnimation>>
    abstract fun playAnimation(animation: CrateAnimation)

    abstract fun skipAnimation(player: Player)

    abstract fun forceStopAnimation(player: Player)
    abstract fun forceStopAnimations()

    abstract fun tick()

}