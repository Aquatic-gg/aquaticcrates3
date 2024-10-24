package gg.aquatic.aquaticcrates.api.animation.crate

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticseries.lib.interactable2.SpawnedInteractable
import org.bukkit.entity.Player
import org.bukkit.util.Vector

abstract class CrateAnimation: Animation() {

    abstract val animationManager: CrateAnimationManager

    abstract val state: State

    abstract fun begin()
    abstract fun start()
    fun reroll() {
    }
    abstract fun end()
    abstract fun spawnReward(
        rumblingLength: Int,
        rumblingPeriod: Int,
        aliveLength: Int,
        vector: Vector,
        gravity: Boolean,
        offset: Vector
    )

    //abstract fun getVisual(): SpawnedInteractable<*>

    enum class State {
        PRE_OPEN,
        OPENING,
        ROLLING,
        POST_OPEN,
        FINISHED,
    }

}