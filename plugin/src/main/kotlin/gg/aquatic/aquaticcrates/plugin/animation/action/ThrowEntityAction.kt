package gg.aquatic.aquaticcrates.plugin.animation.action

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.util.VectorArgument
import gg.aquatic.aquaticcrates.plugin.animation.prop.ThrowableAnimationProp
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument

class ThrowEntityAction: AbstractAction<Animation>() {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        VectorArgument("velocity", null, false),
        PrimitiveObjectArgument("power", "double", true),
        PrimitiveObjectArgument("prop", "entity:example", true)
    )

    override fun execute(binder: Animation, args: Map<String, Any?>, textUpdater: (Animation, String) -> String) {
        val velocity = args["velocity"] as? org.bukkit.util.Vector? ?: return
        val power = args["power"]?.toString()?.toDouble() ?: return
        val property = args["prop"]?.toString() ?: "entity:example"

        val prop = binder.props[property] ?: return
        if (prop !is ThrowableAnimationProp) return

        prop.throwObject(velocity.clone().normalize().multiply(power))
    }
}