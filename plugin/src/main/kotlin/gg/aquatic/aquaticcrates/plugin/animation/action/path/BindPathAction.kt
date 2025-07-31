package gg.aquatic.aquaticcrates.plugin.animation.action.path

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.plugin.animation.prop.Moveable
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.BoundPathObjectArgument
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathBoundProperties
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathProp
import gg.aquatic.waves.util.action.RegisterAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.generic.Action
import java.util.concurrent.ConcurrentHashMap

@RegisterAction("bind-path")
class BindPathAction : Action<Animation>{
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("object-id", "model", true),
        BoundPathObjectArgument(
            "bound-paths",
            { _ -> ConcurrentHashMap() },
            false
        )
    )

    @Suppress("UNCHECKED_CAST")
    override fun execute(binder: Animation, args: ObjectArguments, textUpdater: (Animation, String) -> String) {
        val objectId = args.string("object-id") { textUpdater(binder, it) } ?: return

        val boundPropertiesFactory = args.any("bound-paths") as ((Animation) -> ConcurrentHashMap<PathProp, PathBoundProperties>)? ?: { _ -> ConcurrentHashMap() }

        val prop = binder.props[objectId] as? Moveable ?: return
        var i = 0
        val boundPaths = boundPropertiesFactory(binder)


        prop.boundPaths += boundPaths.mapValues {
            i++
            it.value to prop.boundPaths.size + i
        }
        for ((path, pathProperties) in boundPaths) {
            path.boundProps += prop to pathProperties
        }
    }
}