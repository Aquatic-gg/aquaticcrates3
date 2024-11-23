package gg.aquatic.aquaticcrates.plugin.animation.action.path

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.LinearPathProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathPoint
import gg.aquatic.aquaticseries.lib.action.AbstractAction
import gg.aquatic.aquaticseries.lib.util.argument.AquaticObjectArgument
import gg.aquatic.aquaticseries.lib.util.argument.impl.PrimitiveObjectArgument
import java.util.*
import java.util.function.BiFunction

class LinearPathAction: AbstractAction<Animation>() {
    @Suppress("UNCHECKED_CAST")
    override fun run(binder: Animation, args: Map<String, Any?>, textUpdater: BiFunction<Animation, String, String>) {
        val id = args["id"] as String
        val points = args["points"] as TreeMap<Int, PathPoint>

        val path = LinearPathProp(
            points, binder
        )
        binder.props["path:$id"] = path
    }

    override fun arguments(): List<AquaticObjectArgument<*>> {
        return listOf(
            PrimitiveObjectArgument("id", "linear-path1", true),
            PathPointsArgument("points", TreeMap<Int, PathPoint>(), true),
        )
    }
}