package gg.aquatic.aquaticcrates.plugin.animation.prop

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationActions
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.waves.util.executeActions
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import org.bukkit.Bukkit

class TickerAnimationProp(
    override val animation: Animation,
    val id: String,
    val tickEvery: Int,
    val actions: CrateAnimationActions,
    val repeatLimit: Int) : AnimationProp() {

    var tick = 0
    var actualTick = 0

    override fun tick() {
        if (repeatLimit > 0 && actualTick >= repeatLimit) {
            return
        }
        tick++
        if (tick >= tickEvery) {
            tick = 0
            actualTick++
            actions.animationActions.executeActions(animation) { _, str ->
                animation.updatePlaceholders(str)
                    .replace("%tick:$id%", actualTick.toString())
            }
            if (animation is PlayerBoundAnimation) {
                actions.playerBoundActions.executeActions(animation) { _, str ->
                    animation.updatePlaceholders(str)
                        .replace("%tick:$id%", actualTick.toString())
                }
            }
        }
    }

    override fun onAnimationEnd() {

    }
}