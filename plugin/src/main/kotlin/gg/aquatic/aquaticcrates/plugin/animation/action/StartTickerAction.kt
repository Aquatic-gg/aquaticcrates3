package gg.aquatic.aquaticcrates.plugin.animation.action

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationActions
import gg.aquatic.aquaticcrates.api.util.ActionsArgument
import gg.aquatic.aquaticcrates.plugin.animation.prop.TickerAnimationProp
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject

class StartTickerAction : AbstractAction<Animation>() {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        ActionsArgument("actions", null, true),
        PrimitiveObjectArgument("tick-every", 1, false),
        PrimitiveObjectArgument("id", "example", false),
        PrimitiveObjectArgument("repeat-limit", -1, false)
    )

    override fun execute(binder: Animation, args: Map<String, Any?>, textUpdater: (Animation, String) -> String) {
        val actions = args["actions"] as? CrateAnimationActions ?: return
        val tickEvery = args["tick-every"] as Int
        val repeatLimit = args["repeat-limit"] as Int
        val id = args["id"] as String

        val prop = TickerAnimationProp(binder, id, tickEvery, actions, repeatLimit)
        binder.props["ticker:$id"] = prop
    }
}