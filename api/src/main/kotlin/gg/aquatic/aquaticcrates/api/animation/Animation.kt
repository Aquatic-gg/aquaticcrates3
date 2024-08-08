package gg.aquatic.aquaticcrates.api.animation

import gg.aquatic.aquaticcrates.api.crate.Model
import gg.aquatic.aquaticcrates.api.reward.Reward
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.function.Consumer

abstract class Animation(
    val animationManager: AnimationManager,
    val player: Player,
    val callBack: Consumer<Animation>
) {
    var started = false

    lateinit var reward: Reward

    companion object {
        val META_KEY = "AquaticCrates_inAnimation"
    }

    abstract fun begin()
    abstract fun start()
    fun reroll() {
        callBack.accept(this)
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

    abstract fun getModel(): Model


}