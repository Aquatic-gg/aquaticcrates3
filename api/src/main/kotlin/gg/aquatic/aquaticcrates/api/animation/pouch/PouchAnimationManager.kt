package gg.aquatic.aquaticcrates.api.animation.pouch

import gg.aquatic.aquaticcrates.api.animation.AnimationTitle
import gg.aquatic.aquaticcrates.api.pouch.Pouch
import org.bukkit.entity.Player
import java.util.*

abstract class PouchAnimationManager {

    abstract val pouch: Pouch
    abstract val animationSettings: PouchAnimationSettings

    open fun showAnimationTitle(title: AnimationTitle, player: Player) {

    }

    open fun hideAnimationTitle(title: AnimationTitle, player: Player) {

    }

    abstract val playingAnimations: HashMap<UUID, MutableList<PouchAnimation>>

    abstract fun skipAnimation(player: Player)

    abstract fun forceStopAnimation(player: Player)

    abstract fun tick()


}