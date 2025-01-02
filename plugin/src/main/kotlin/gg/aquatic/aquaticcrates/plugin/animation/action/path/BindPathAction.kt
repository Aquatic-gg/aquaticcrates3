package gg.aquatic.aquaticcrates.plugin.animation.action.path

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.plugin.animation.prop.MovableAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.BoundPathObjectArgument
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathBoundProperties
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathProp
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import org.bukkit.Bukkit
import java.util.concurrent.ConcurrentHashMap

class BindPathAction : AbstractAction<Animation>(){
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("object-id", "model", true),
        BoundPathObjectArgument(
            "bound-paths",
            { _ -> ConcurrentHashMap() },
            false
        )
    )

    override fun execute(binder: Animation, args: Map<String, Any?>, textUpdater: (Animation, String) -> String) {
        val objectId = args["object-id"] as String

        val boundPropertiesFactory = args["bound-paths"] as ((Animation) -> ConcurrentHashMap<PathProp, PathBoundProperties>)? ?: { _ -> ConcurrentHashMap() }
        val boundPaths = boundPropertiesFactory(binder)

        val prop = binder.props[objectId] as? MovableAnimationProp ?: return

        prop.boundPaths += boundPaths
        for ((path, pathProperties) in boundPaths) {
            Bukkit.getConsoleSender().sendMessage("Binding entity to the animation!")
            path.boundProps += prop to pathProperties
        }
    }
}