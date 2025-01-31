package gg.aquatic.aquaticcrates.plugin.animation.prop.timer

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationActions
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp

class LaterActionsAnimationProp(
    override val animation: Animation, val action: CrateAnimationActions, val runAfter: Int
) : AnimationProp() {

    var tick = 0
        private set

    private var finished = false

    override fun tick() {
        if (finished) return
        if (tick >= runAfter) {
            action.execute(animation)
        }
    }

    override fun onAnimationEnd() {

    }
}