package gg.aquatic.aquaticcrates.plugin.animation.action

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.waves.util.action.RegisterAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.generic.Action

@RegisterAction("variable")
class VariableAction: Action<Animation> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("id", "example", true),
        PrimitiveObjectArgument("value", "example", true)
    )

    override fun execute(
        binder: Animation,
        args: ObjectArguments,
        textUpdater: (Animation, String) -> String
    ) {
        val id = args.string("id") { textUpdater(binder, it) } ?: return
        val value = args.string("value") { textUpdater(binder, it) } ?: return
        binder.extraPlaceholders["variable:$id"] = { str -> str.replace("%variable:$id%", value) }
    }
}