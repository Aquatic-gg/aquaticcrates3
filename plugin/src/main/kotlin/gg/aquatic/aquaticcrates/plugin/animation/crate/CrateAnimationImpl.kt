package gg.aquatic.aquaticcrates.plugin.animation.crate

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationManager
import gg.aquatic.aquaticseries.lib.interactable2.SpawnedInteractable
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class CrateAnimationImpl(
    override val animationManager: CrateAnimationManager,
    override val player: Player
) : CrateAnimation() {
    override fun begin() {
        TODO("Not yet implemented")
    }

    override fun start() {
        TODO("Not yet implemented")
    }

    override fun end() {
        TODO("Not yet implemented")
    }

    override fun spawnReward(
        rumblingLength: Int,
        rumblingPeriod: Int,
        aliveLength: Int,
        vector: Vector,
        gravity: Boolean,
        offset: Vector
    ) {
        TODO("Not yet implemented")
    }

    override fun getVisual(): SpawnedInteractable<*> {
        TODO("Not yet implemented")
    }
}