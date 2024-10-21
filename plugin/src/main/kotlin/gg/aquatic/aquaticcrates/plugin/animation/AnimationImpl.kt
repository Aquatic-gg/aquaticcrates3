package gg.aquatic.aquaticcrates.plugin.animation

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.AnimationManager
import gg.aquatic.aquaticseries.lib.interactable2.SpawnedInteractable
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class AnimationImpl(
    override val animationManager: AnimationManager,
    override val player: Player
) : Animation() {
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