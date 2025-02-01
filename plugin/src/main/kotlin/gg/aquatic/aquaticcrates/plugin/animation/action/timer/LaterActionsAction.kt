package gg.aquatic.aquaticcrates.plugin.animation.action.timer

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationActions
import gg.aquatic.aquaticcrates.api.util.ActionsArgument
import gg.aquatic.aquaticcrates.plugin.animation.prop.timer.LaterActionsAnimationProp
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import java.util.UUID

class LaterActionsAction: AbstractAction<Animation>() {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("delay", 0, true),
        ActionsArgument("actions", null, true),
    )

    override fun execute(binder: Animation, args: Map<String, Any?>, textUpdater: (Animation, String) -> String) {
        val delay = args["delay"] as Int
        val actions = args["actions"] as? CrateAnimationActions ?: return
        val prop = LaterActionsAnimationProp(
            binder,
            actions,
            delay
        )
        binder.props["later-actions:${UUID.randomUUID()}"] = prop
    }
}