package gg.aquatic.aquaticcrates.plugin.animation.crate

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationManager
import gg.aquatic.aquaticcrates.api.animation.AnimationTitle
import gg.aquatic.aquaticcrates.api.animation.pouch.PouchAnimation
import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate
import gg.aquatic.aquaticcrates.api.animation.SpawnedRewardVisual
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationSettings
import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.HashMap

class CrateAnimationManagerImpl(
    override val crate: Crate,
    override val animationSettings: CrateAnimationSettings,
) : CrateAnimationManager() {
    override val spawnedRewardVisuals: MutableList<SpawnedRewardVisual> = mutableListOf()
    override val playingAnimations: HashMap<UUID, CrateAnimation> = hashMapOf()

    override fun open(player: Player, spawnedCrate: SpawnedCrate?) {
        spawnedCrate?.apply {
            val interactable = this.spawnedInteractable
            // TODO: Play crate animation
        }
    }

    override fun playAnimationTask(time: Int, animation: CrateAnimation) {
        TODO("Not yet implemented")
    }

    override fun shouldStopAnimation(time: Int, animation: CrateAnimation): Boolean {
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