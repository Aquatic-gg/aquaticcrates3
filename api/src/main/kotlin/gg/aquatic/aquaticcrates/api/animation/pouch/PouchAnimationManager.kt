package gg.aquatic.aquaticcrates.api.animation.pouch

import gg.aquatic.aquaticcrates.api.animation.AnimationTitle
import gg.aquatic.aquaticcrates.api.pouch.Pouch
import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import org.bukkit.entity.Player
import java.util.*

abstract class PouchAnimationManager {

    abstract val pouch: Pouch
    abstract val animationTasks: TreeMap<Int, MutableList<ConfiguredAction<PouchAnimation>>>
    abstract val animationLength: Int
    abstract val preAnimationDelay: Int
    abstract val preAnimationTasks: TreeMap<Int, MutableList<ConfiguredAction<PouchAnimation>>>
    abstract val postAnimationDelay: Int
    abstract val postAnimationTasks: TreeMap<Int, MutableList<ConfiguredAction<PouchAnimation>>>
    abstract val finalAnimationTasks: MutableList<ConfiguredAction<PouchAnimation>>
    abstract val skippable: Boolean
    abstract val openingBossbar: AnimationTitle

    open fun showAnimationTitle(title: AnimationTitle, player: Player) {

    }

    open fun hideAnimationTitle(title: AnimationTitle, player: Player) {

    }

    abstract val playingAnimations: HashMap<UUID, PouchAnimation>

    abstract fun skipAnimation(player: Player)

    abstract fun forceStopAnimation(player: Player)

    abstract fun tick()


}