package gg.aquatic.aquaticcrates.plugin.animation.action.model

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.plugin.animation.prop.model.ModelAnimationProp
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument

class PlayModelAnimationAction: AbstractAction<Animation>() {

    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("id", "model", true),
        PrimitiveObjectArgument("animation", "animation", true),
        PrimitiveObjectArgument("fade-in", 0.0, false),
        PrimitiveObjectArgument("fade-out", 0.0, false),
        PrimitiveObjectArgument("speed", 1.0, false),
    )

    override fun execute(binder: Animation, args: ObjectArguments, textUpdater: (Animation, String) -> String) {
        val id = args.string("id") { textUpdater(binder, it) } ?: return
        val animation = args.string("animation") { textUpdater(binder, it) } ?: return
        val fadeIn = args.double("fade-in") { textUpdater(binder, it) } ?: return
        val fadeOut = args.double("fade-out") { textUpdater(binder, it) } ?: return
        val speed = args.double("speed") { textUpdater(binder, it) } ?: return

        val prop = binder.props["model:$id"] as? ModelAnimationProp ?: return
        prop.playAnimation(animation, fadeIn, fadeOut, speed)
    }
}