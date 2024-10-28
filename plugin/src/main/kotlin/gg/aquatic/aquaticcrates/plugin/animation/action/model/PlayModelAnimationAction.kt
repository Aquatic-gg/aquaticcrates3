package gg.aquatic.aquaticcrates.plugin.animation.action.model

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.plugin.animation.prop.model.ModelAnimationProp
import gg.aquatic.aquaticseries.lib.action.AbstractAction
import gg.aquatic.aquaticseries.lib.util.argument.AquaticObjectArgument
import gg.aquatic.aquaticseries.lib.util.argument.impl.PrimitiveObjectArgument
import java.util.function.BiFunction

class PlayModelAnimationAction: AbstractAction<Animation>() {
    override fun run(binder: Animation, args: Map<String, Any?>, textUpdater: BiFunction<Animation, String, String>) {
        val id = args["id"] as String
        val animation = args["animation"] as String
        val fadeIn = args["fade-in"] as Double
        val fadeOut = args["fade-out"] as Double
        val speed = args["speed"] as Double

        val prop = binder.props["model:$id"] as? ModelAnimationProp ?: return
        prop.playAnimation(animation, fadeIn, fadeOut, speed)
    }

    override fun arguments(): List<AquaticObjectArgument<*>> {
        return listOf(
            PrimitiveObjectArgument("id", "model", true),
            PrimitiveObjectArgument("animation", "animation", true),
            PrimitiveObjectArgument("fade-in", 0.0, false),
            PrimitiveObjectArgument("fade-out", 0.0, false),
            PrimitiveObjectArgument("speed", 1.0, false),
        )
    }
}