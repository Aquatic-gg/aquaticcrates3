package gg.aquatic.aquaticcrates.plugin.animation.action.path

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathPoint
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.SmoothPathProp
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import java.util.*

class SmoothPathAction: AbstractAction<Animation>() {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("id", "smooth-path1", true),
        PathPointsArgument("points", TreeMap<Int, PathPoint>(), true),
    )

    override fun execute(binder: Animation, args: Map<String, Any?>, textUpdater: (Animation, String) -> String) {
        val id = args["id"] as String
        val points = args["points"] as TreeMap<Int, PathPoint>
        val path = SmoothPathProp(
            binder, points
        )
        binder.props["path:$id"] = path
    }
}