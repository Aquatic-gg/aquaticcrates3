package gg.aquatic.aquaticcrates.api.animation

import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticseries.lib.interactable2.SpawnedInteractable
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.function.Consumer

abstract class Animation {

    abstract val animationManager: AnimationManager
    abstract val player: Player

    var started = false

    lateinit var reward: Reward

    companion object {
        val META_KEY = "AquaticCrates_inAnimation"
    }

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

    abstract fun getVisual(): SpawnedInteractable<*>


}