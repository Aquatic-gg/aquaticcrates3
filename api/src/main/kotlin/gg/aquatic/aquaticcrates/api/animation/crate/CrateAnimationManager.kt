package gg.aquatic.aquaticcrates.api.animation.crate

import gg.aquatic.aquaticcrates.api.animation.AnimationTitle
import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.reroll.RerollManager
import org.bukkit.entity.Player
import java.util.HashMap
import java.util.UUID

abstract class CrateAnimationManager {

    abstract val crate: Crate
    abstract val animationSettings: CrateAnimationSettings
    abstract val rerollManager: RerollManager

    open fun showAnimationTitle(title: AnimationTitle, player: Player) {

    }

    open fun hideAnimationTitle(title: AnimationTitle, player: Player) {

    }

    abstract val playingAnimations: HashMap<UUID, MutableList<CrateAnimation>>

    abstract fun skipAnimation(player: Player)

    abstract fun forceStopAnimation(player: Player)

    abstract fun tick()

}