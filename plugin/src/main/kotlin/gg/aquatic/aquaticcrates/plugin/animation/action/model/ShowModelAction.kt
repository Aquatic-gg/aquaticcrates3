package gg.aquatic.aquaticcrates.plugin.animation.action.model

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.api.util.VectorArgument
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.BoundPathObjectArgument
import gg.aquatic.aquaticcrates.plugin.animation.prop.model.ModelAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathBoundProperties
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathProp
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import org.bukkit.Bukkit
import org.bukkit.util.Vector
import java.util.concurrent.ConcurrentHashMap

class ShowModelAction : AbstractAction<Animation>() {

    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("id", "example", true),
        PrimitiveObjectArgument("model", "", true),
        PrimitiveObjectArgument("apply-skin", true, required = false),
        PrimitiveObjectArgument("animation", null, false),
        VectorArgument("location-offset", Vector(), false),
        BoundPathObjectArgument(
            "bound-paths",
            { _ -> ConcurrentHashMap() },
            false
        )
    )

    override fun execute(binder: Animation, args: Map<String, Any?>, textUpdater: (Animation, String) -> String) {
        val id = args["id"] as String
        val model = args["model"] as String
        val applySkin = args["apply-skin"] as Boolean
        val animation = args["animation"] as String?
        val boundPropertiesFactory =
            args["bound-paths"] as? ((Animation) -> ConcurrentHashMap<PathProp, PathBoundProperties>)
                ?: { _ -> ConcurrentHashMap() }

        val boundPaths = boundPropertiesFactory(binder)
        var i = 0
        val prop = ModelAnimationProp(
            binder,
            model,
            if (applySkin && binder is PlayerBoundAnimation) binder.player else null,
            animation,
            args["location-offset"] as Vector,
            ConcurrentHashMap(boundPaths.mapValues {
                i++
                it.value to i
            })
        )

        for ((path, pathProperties) in boundPaths) {
            path.boundProps += prop to pathProperties
        }

        binder.props["model:$id"] = prop
    }
}