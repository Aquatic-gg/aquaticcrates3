package gg.aquatic.aquaticcrates.plugin.animation.prop

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.waves.util.executeActions
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject

class TickerAnimationProp(
    override val animation: Animation,
    val id: String,
    val tickEvery: Int,
    val actions: List<ConfiguredExecutableObject<Animation,Unit>>,
    val repeatLimit: Int) : AnimationProp() {

    var tick = 0
    var actualTick = 0

    override fun tick() {
        if (repeatLimit in 1..<actualTick) {
            return
        }
        tick++
        if (tick >= tickEvery) {
            tick = 0
            actualTick++
            actions.executeActions(animation) { _, str ->
                animation.updatePlaceholders(str)
                    .replace("%tick:$id%", actualTick.toString())
            }
        }
    }

    override fun onAnimationEnd() {

    }
}