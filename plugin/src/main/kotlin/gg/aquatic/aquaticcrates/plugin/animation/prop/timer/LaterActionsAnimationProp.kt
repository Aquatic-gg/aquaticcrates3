package gg.aquatic.aquaticcrates.plugin.animation.prop.timer

import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.waves.util.collection.executeActions
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject

class LaterActionsAnimationProp(
    override val animation: PlayerBoundAnimation, val action: Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>, val runAfter: Int
) : AnimationProp() {

    var tick = 0
        private set

    private var finished = false

    override fun tick() {
        if (finished) return
        if (tick >= runAfter) {
            action.executeActions(animation) { a, str -> a.updatePlaceholders(str) }
        }
    }

    override fun onAnimationEnd() {

    }
}