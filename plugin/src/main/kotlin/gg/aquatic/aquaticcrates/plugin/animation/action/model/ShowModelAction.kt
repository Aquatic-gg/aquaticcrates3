package gg.aquatic.aquaticcrates.plugin.animation.action.model

import gg.aquatic.aquaticcrates.plugin.animation.prop.model.ModelAnimationProp
import gg.aquatic.waves.scenario.PlayerScenario
import gg.aquatic.waves.scenario.Scenario
import gg.aquatic.waves.scenario.action.path.BoundPathObjectArgument
import gg.aquatic.waves.scenario.prop.path.PathBoundProperties
import gg.aquatic.waves.scenario.prop.path.PathProp
import gg.aquatic.waves.util.action.RegisterAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.generic.Action
import net.kyori.adventure.key.Key
import org.bukkit.Color
import org.bukkit.util.Vector
import java.util.concurrent.ConcurrentHashMap

@RegisterAction("show-model")
class ShowModelAction : Action<Scenario> {

    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("id", "example", true),
        PrimitiveObjectArgument("model", "", true),
        PrimitiveObjectArgument("apply-skin", true, required = false),
        PrimitiveObjectArgument("animation", null, false),
        PrimitiveObjectArgument("tint","255;255;255", false),
        PrimitiveObjectArgument("location-offset", "0;0;0", false),
        BoundPathObjectArgument(
            "bound-paths",
            { _ -> ConcurrentHashMap() },
            false
        )
    )

    @Suppress("UNCHECKED_CAST")
    override fun execute(binder: Scenario, args: ObjectArguments, textUpdater: (Scenario, String) -> String) {
        val id = args.string("id") { textUpdater(binder, it) } ?: return
        val model = args.string("model") { textUpdater(binder, it) } ?: return
        val applySkin = args.boolean("apply-skin") { textUpdater(binder, it) } ?: true
        val tint = args.string("tint") { textUpdater(binder, it) }
        val animation = args.string("animation") { textUpdater(binder, it) }
        val boundPropertiesFactory =
            args.any("bound-paths") as? ((Scenario) -> ConcurrentHashMap<PathProp, PathBoundProperties>)
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

        val tintColor = tint?.let {
            val split = it.split(";")
            Color.fromRGB(split[0].toInt(), split[1].toInt(), split[2].toInt())
        }

        val key = Key.key("model:$id")
        binder.props[key]?.onEnd()
        val boundPaths = boundPropertiesFactory(binder)
        var i = 0
        val prop = ModelAnimationProp(
            binder,
            model,
            if (applySkin && binder is PlayerScenario) binder.player else null,
            tintColor,
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

        binder.props[key] = prop
    }
}