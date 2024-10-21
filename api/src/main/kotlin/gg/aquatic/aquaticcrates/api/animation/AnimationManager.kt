package gg.aquatic.aquaticcrates.api.animation

import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate
import gg.aquatic.aquaticcrates.api.reward.SpawnedRewardVisual
import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import org.bukkit.entity.Player
import java.util.TreeMap
import java.util.UUID

abstract class AnimationManager {

    abstract val crate: Crate
    abstract val animationTasks: TreeMap<Int,MutableList<ConfiguredAction<Animation>>>
    abstract val animationLength: Int
    abstract val preAnimationDelay: Int
    abstract val preAnimationTasks: MutableList<ConfiguredAction<Animation>>
    abstract val postAnimationTasks: MutableList<ConfiguredAction<Animation>>
    abstract val skippable: Boolean
    abstract val openingBossbar: AnimationTitle
    abstract val rerollingBossbar: AnimationTitle

    abstract val playingAnimations: HashMap<UUID,Animation>
    abstract val spawnedRewardVisuals: MutableList<SpawnedRewardVisual>

    abstract fun open(player: Player, spawnedCrate: SpawnedCrate?)
    abstract fun playAnimationTask(time: Int, animation: Animation)
    abstract fun shouldStopAnimation(time: Int, animation: Animation): Boolean
    abstract fun canBeOpened(player: Player): Boolean
    abstract fun isAnyoneOpening(): Boolean

    open fun showAnimationTitle(title: AnimationTitle, player: Player) {

    }

    open fun hideAnimationTitle(title: AnimationTitle, player: Player) {

    }

    abstract fun skipAnimation(player: Player)

    abstract fun forceStopAnimation(player: Player)

}