package gg.aquatic.aquaticcrates.plugin.animation

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.AnimationManager
import gg.aquatic.aquaticcrates.api.animation.AnimationTitle
import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate
import gg.aquatic.aquaticcrates.api.reward.SpawnedRewardVisual
import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.HashMap

class PlacedCrateAnimationManager(
    override val crate: Crate,
    override val animationTasks: TreeMap<Int, MutableList<ConfiguredAction<Animation>>>,
    override val animationLength: Int,
    override val preAnimationDelay: Int,
    override val preAnimationTasks: MutableList<ConfiguredAction<Animation>>,
    override val postAnimationTasks: MutableList<ConfiguredAction<Animation>>,
    override val skippable: Boolean,
    override val openingBossbar: AnimationTitle,
    override val rerollingBossbar: AnimationTitle,
    override val playingAnimations: HashMap<UUID, Animation>
) : AnimationManager() {
    override val spawnedRewardVisuals: MutableList<SpawnedRewardVisual> = mutableListOf()

    override fun open(player: Player, spawnedCrate: SpawnedCrate?) {
        spawnedCrate?.apply {
            val interactable = this.spawnedInteractable
            // TODO: Play crate animation
        }
    }

    override fun playAnimationTask(time: Int, animation: Animation) {
        TODO("Not yet implemented")
    }

    override fun shouldStopAnimation(time: Int, animation: Animation): Boolean {
        TODO("Not yet implemented")
    }

    override fun canBeOpened(player: Player): Boolean {
        TODO("Not yet implemented")
    }

    override fun isAnyoneOpening(): Boolean {
        TODO("Not yet implemented")
    }

    override fun skipAnimation(player: Player) {
        TODO("Not yet implemented")
    }

    override fun forceStopAnimation(player: Player) {
        TODO("Not yet implemented")
    }
}