package gg.aquatic.aquaticcrates.plugin.animation.action.model

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.plugin.animation.prop.model.ModelAnimationProp
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument

class PlayModelAnimationAction: AbstractAction<Animation>() {

    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("id", "model", true),
        PrimitiveObjectArgument("animation", "animation", true),
        PrimitiveObjectArgument("fade-in", 0.0, false),
        PrimitiveObjectArgument("fade-out", 0.0, false),
        PrimitiveObjectArgument("speed", 1.0, false),
    )

    override fun execute(binder: Animation, args: Map<String, Any?>, textUpdater: (Animation, String) -> String) {
        val id = args["id"] as String
        val animation = args["animation"] as String
        val fadeIn = args["fade-in"] as Double
        val fadeOut = args["fade-out"] as Double
        val speed = args["speed"] as Double

        val prop = binder.props["model:$id"] as? ModelAnimationProp ?: return
        prop.playAnimation(animation, fadeIn, fadeOut, speed)
    }
}