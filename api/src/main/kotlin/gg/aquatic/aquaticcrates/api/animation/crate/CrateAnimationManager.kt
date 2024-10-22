package gg.aquatic.aquaticcrates.api.animation.crate

import gg.aquatic.aquaticcrates.api.animation.AnimationTitle
import gg.aquatic.aquaticcrates.api.animation.pouch.PouchAnimation
import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate
import gg.aquatic.aquaticcrates.api.reward.SpawnedRewardVisual
import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import org.bukkit.entity.Player
import java.util.TreeMap
import java.util.UUID

abstract class CrateAnimationManager {

    abstract val crate: Crate
    abstract val animationTasks: TreeMap<Int, MutableList<ConfiguredAction<CrateAnimation>>>
    abstract val animationLength: Int
    abstract val preAnimationDelay: Int
    abstract val preAnimationTasks: TreeMap<Int, MutableList<ConfiguredAction<CrateAnimation>>>
    abstract val postAnimationDelay: Int
    abstract val postAnimationTasks: TreeMap<Int, MutableList<ConfiguredAction<CrateAnimation>>>
    abstract val finalAnimationTasks: MutableList<ConfiguredAction<PouchAnimation>>
    abstract val skippable: Boolean
    abstract val openingBossbar: AnimationTitle
    abstract val rerollingBossbar: AnimationTitle

    abstract val playingAnimations: HashMap<UUID, CrateAnimation>
    abstract val spawnedRewardVisuals: MutableList<SpawnedRewardVisual>

    abstract val state: State

    abstract fun open(player: Player, spawnedCrate: SpawnedCrate?)
    abstract fun playAnimationTask(time: Int, animation: CrateAnimation)
    abstract fun shouldStopAnimation(time: Int, animation: CrateAnimation): Boolean
    abstract fun canBeOpened(player: Player): Boolean
    abstract fun isAnyoneOpening(): Boolean

    open fun showAnimationTitle(title: AnimationTitle, player: Player) {

    }

    open fun hideAnimationTitle(title: AnimationTitle, player: Player) {

    }

    abstract fun skipAnimation(player: Player)

    abstract fun forceStopAnimation(player: Player)

    enum class State {
        PRE_OPEN,
        OPENING,
        ROLLING,
        POST_OPEN,
        FINISHED,
    }

}