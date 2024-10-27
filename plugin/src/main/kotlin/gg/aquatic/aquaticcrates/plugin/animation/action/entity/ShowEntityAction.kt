package gg.aquatic.aquaticcrates.plugin.animation.action.entity

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.util.VectorArgument
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.EntityAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityPropertiesObjectArgument
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathProp
import gg.aquatic.aquaticseries.lib.action.AbstractAction
import gg.aquatic.aquaticseries.lib.util.argument.AquaticObjectArgument
import gg.aquatic.aquaticseries.lib.util.argument.impl.PrimitiveObjectArgument
import org.bukkit.Bukkit
import org.bukkit.util.Vector
import java.util.function.BiFunction

class ShowEntityAction: AbstractAction<Animation>() {
    override fun run(binder: Animation, args: Map<String, Any?>, textUpdater: BiFunction<Animation, String, String>) {
        val id = args["id"] as String
        val type = args["entity-type"] as String
        val properties = args["properties"] as List<EntityProperty>
        val locationOffset = args["location-offset"] as? Vector? ?: Vector()
        val pathLocationOffset = args["path-location-offset"] as? Vector? ?: Vector()
        val boundPathId = args["bound-path"] as? String?

        val boundPath = if (boundPathId != null) {
            val path = binder.props["path:$boundPathId"] as? PathProp
            if (path != null) {
                Bukkit.broadcastMessage("Binding entity to path: $boundPathId")
            }
            path
        } else null

        val entity = EntityAnimationProp(
            binder,
            locationOffset,
            pathLocationOffset,
            boundPath,
            type,
            properties
        )

        boundPath?.boundProps?.add(entity)

        binder.props["entity:$id"] = entity
    }

    override fun arguments(): List<AquaticObjectArgument<*>> {
        return listOf(
            PrimitiveObjectArgument("id", "example-entity", true),
            PrimitiveObjectArgument("entity-type", "zombie", true),
            EntityPropertiesObjectArgument("properties", listOf(), false),
            VectorArgument("location-offset", Vector(), false),
            VectorArgument("path-location-offset", Vector(), false),
            PrimitiveObjectArgument("bound-path", null, false),
        )
    }
}