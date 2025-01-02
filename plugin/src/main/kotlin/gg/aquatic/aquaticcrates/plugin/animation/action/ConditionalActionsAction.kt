package gg.aquatic.aquaticcrates.plugin.animation.action

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.util.ConditionalActionsArgument
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.generic.ConfiguredExecutableObjectsWithConditions

class ConditionalActionsAction: AbstractAction<Animation>() {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        ConditionalActionsArgument("", null, true)
    )

    override fun execute(binder: Animation, args: Map<String, Any?>, textUpdater: (Animation, String) -> String) {
        val actions = args["actions"] as ConfiguredExecutableObjectsWithConditions<Animation,Unit>?

        actions?.tryExecute(binder, textUpdater)
    }
}