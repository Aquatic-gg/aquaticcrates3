package gg.aquatic.aquaticcrates.plugin.animation.action.entity

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.util.VectorArgument
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.BoundPathObjectArgument
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.EntityAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityPropertiesObjectArgument
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathBoundProperties
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathProp
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import org.bukkit.Bukkit
import org.bukkit.util.Vector
import java.util.concurrent.ConcurrentHashMap

class ShowEntityAction : AbstractAction<Animation>() {

    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("id", "example-entity", true),
        PrimitiveObjectArgument("entity-type", "zombie", true),
        EntityPropertiesObjectArgument("properties", listOf(), false),
        VectorArgument("location-offset", Vector(), false),
        BoundPathObjectArgument(
            "bound-paths",
            { _ -> ConcurrentHashMap() },
            false
        )
    )

    override fun execute(binder: Animation, args: Map<String, Any?>, textUpdater: (Animation, String) -> String) {
        val id = args["id"] as String
        val type = args["entity-type"] as String
        val properties = args["properties"] as List<EntityProperty>
        val locationOffset = args["location-offset"] as? Vector? ?: Vector()
        val boundPropertiesFactory = args["bound-paths"] as ((Animation) -> ConcurrentHashMap<PathProp, PathBoundProperties>)? ?: { _ -> ConcurrentHashMap() }

        val boundPaths = boundPropertiesFactory(binder)
        Bukkit.getConsoleSender().sendMessage("Bound paths: ${boundPaths.size}")

        val entity = EntityAnimationProp(
            binder,
            locationOffset,
            boundPaths,
            type,
            properties
        )

        for ((path, pathProperties) in boundPaths) {
            Bukkit.getConsoleSender().sendMessage("Binding entity to the animation!")
            path.boundProps += entity to pathProperties
        }

        binder.props["entity:$id"] = entity
    }
}