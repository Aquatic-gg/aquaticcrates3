package gg.aquatic.aquaticcrates.plugin.animation.prop.timer

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationActions
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.waves.util.executeActions

class TickerAnimationProp(
    override val animation: Animation,
    val id: String,
    val tickEvery: Int,
    val actions: CrateAnimationActions,
    val repeatLimit: Int) : AnimationProp() {

    var tick = 0
    var actualTick = 0

    init {
        animation.extraPlaceholders += "tick:$id" to { str ->
            str.replace("%tick:$id%", actualTick.toString())
        }
    }

    override fun tick() {
        if (repeatLimit > 0 && actualTick >= repeatLimit) {
            return
        }
        tick++
        if (tick >= tickEvery) {
            tick = 0
            actualTick++
            actions.execute(animation)
        }
    }

    override fun onAnimationEnd() {

    }
}