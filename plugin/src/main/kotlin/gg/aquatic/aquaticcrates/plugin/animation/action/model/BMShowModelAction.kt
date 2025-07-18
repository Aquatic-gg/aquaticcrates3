package gg.aquatic.aquaticcrates.plugin.animation.action.model

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.BoundPathObjectArgument
import gg.aquatic.aquaticcrates.plugin.animation.prop.model.BMModelAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathBoundProperties
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathProp
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.generic.Action
import org.bukkit.util.Vector
import java.util.concurrent.ConcurrentHashMap

class BMShowModelAction : Action<Animation> {

    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("id", "example", true),
        PrimitiveObjectArgument("model", "", true),
        PrimitiveObjectArgument("animation", null, false),
        PrimitiveObjectArgument("location-offset", "0;0;0", false),
        BoundPathObjectArgument(
            "bound-paths",
            { _ -> ConcurrentHashMap() },
            false
        )
    )

    @Suppress("UNCHECKED_CAST")
    override fun execute(binder: Animation, args: ObjectArguments, textUpdater: (Animation, String) -> String) {
        val id = args.string("id") { textUpdater(binder, it) } ?: return
        val model = args.string("model") { textUpdater(binder, it) } ?: return
        val animation = args.string("animation") { textUpdater(binder, it) }
        val boundPropertiesFactory =
            args.any("bound-paths") as? ((Animation) -> ConcurrentHashMap<PathProp, PathBoundProperties>)
                ?: { _ -> ConcurrentHashMap() }

        val locationOffsetStrings = (args.string("location-offset") { textUpdater(binder, it) })?.split(";") ?: listOf()
        val locationOffsetVector = Vector(
            locationOffsetStrings.getOrNull(0)?.toDoubleOrNull() ?: 0.0,
            locationOffsetStrings.getOrNull(1)?.toDoubleOrNull() ?: 0.0,
            locationOffsetStrings.getOrNull(2)?.toDoubleOrNull() ?: 0.0,
        )
        val locationOffsetYawPitch =
            (locationOffsetStrings.getOrNull(3)?.toFloatOrNull() ?: 0.0f) to (locationOffsetStrings.getOrNull(4)
                ?.toFloatOrNull() ?: 0.0f)

        val boundPaths = boundPropertiesFactory(binder)
        var i = 0
        val prop = BMModelAnimationProp(
            binder,
            model,
            animation,
            locationOffsetVector,
            ConcurrentHashMap(boundPaths.mapValues {
                i++
                it.value to i
            }),
            locationOffsetYawPitch
        )

        for ((path, pathProperties) in boundPaths) {
            path.boundProps += prop to pathProperties
        }

        binder.props["model:$id"] = prop
    }
}