package gg.aquatic.aquaticcrates.plugin.animation.action

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.util.VectorArgument
import gg.aquatic.aquaticcrates.plugin.animation.prop.ThrowableAnimationProp
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.generic.Action

class ThrowEntityAction : Action<Animation> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        VectorArgument("velocity", null, false),
        PrimitiveObjectArgument("power", 1.0, false),
        PrimitiveObjectArgument("prop", "entity:example", true)
    )

    override fun execute(binder: Animation, args: ObjectArguments, textUpdater: (Animation, String) -> String) {
        val velocity = args.vector("velocity") { textUpdater(binder, it) } ?: return
        val power = args.double("power") { textUpdater(binder, it) } ?: 0.0
        val property = args.string("prop") { textUpdater(binder, it) } ?: "entity:example"

        val prop = binder.props[property] ?: return
        if (prop !is ThrowableAnimationProp) return

        prop.throwObject(velocity.clone().multiply(power))
    }
}