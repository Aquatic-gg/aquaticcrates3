package gg.aquatic.aquaticcrates.plugin.animation.action.path

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathPoint
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.SmoothPathProp
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.generic.Action
import java.util.*

@gg.aquatic.waves.util.action.RegisterAction("smooth-path")
class SmoothPathAction: Action<Animation> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("id", "smooth-path1", true),
        PathPointsArgument("points", TreeMap<Int, PathPoint>(), true),
    )

    override fun execute(binder: Animation, args: ObjectArguments, textUpdater: (Animation, String) -> String) {
        val id = args.string("id") { textUpdater(binder, it) } ?: return
        val points = args.typed<TreeMap<Int, PathPoint>>("points") ?: return
        val path = SmoothPathProp(
            binder, points
        )
        binder.props["path:$id"] = path
    }
}