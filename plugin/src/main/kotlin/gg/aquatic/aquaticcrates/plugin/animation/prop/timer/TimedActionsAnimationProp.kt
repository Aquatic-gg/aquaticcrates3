package gg.aquatic.aquaticcrates.plugin.animation.prop.timer

import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.waves.util.collection.executeActions
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject

class TimedActionsAnimationProp(override val animation: PlayerBoundAnimation, val actions: HashMap<Int, Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>>) :
    AnimationProp() {

    var tick = 0
        private set

    override fun tick() {
        actions[tick]?.executeActions(animation) { a, str -> a.updatePlaceholders(str) }
        tick++
    }

    override fun onAnimationEnd() {
    }
}