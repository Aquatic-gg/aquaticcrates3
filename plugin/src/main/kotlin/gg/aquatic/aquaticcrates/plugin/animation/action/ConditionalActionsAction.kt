package gg.aquatic.aquaticcrates.plugin.animation.action

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.util.ConditionalActionsArgument
import gg.aquatic.waves.util.action.RegisterAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.generic.Action

@RegisterAction("conditional-actions")
class ConditionalActionsAction: Action<Animation> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        ConditionalActionsArgument("actions", null, true)
    )

    override fun execute(binder: Animation, args: ObjectArguments, textUpdater: (Animation, String) -> String) {
        val actions = args.typed<ConditionalActionsArgument.ConditionalAnimationActions>("actions")

        actions?.tryExecute(binder)
    }
}