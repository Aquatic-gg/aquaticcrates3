package gg.aquatic.aquaticcrates.plugin.animation.action.model

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticseries.lib.action.AbstractAction
import gg.aquatic.aquaticseries.lib.util.argument.AquaticObjectArgument
import gg.aquatic.aquaticseries.lib.util.argument.impl.PrimitiveObjectArgument
import java.util.function.BiFunction

class HideModelAction: AbstractAction<Animation>() {
    override fun run(binder: Animation, args: Map<String, Any?>, textUpdater: BiFunction<Animation, String, String>) {
        val id = args["id"] as String
        val prop = binder.props["model:$id"] ?: return
        prop.onAnimationEnd()
        binder.props.remove("model:$id")
    }

    override fun arguments(): List<AquaticObjectArgument<*>> {
        return listOf(
            PrimitiveObjectArgument("id", "model", true),
        )
    }
}