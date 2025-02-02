package gg.aquatic.aquaticcrates.plugin.animation.action.entity

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.BoundPathObjectArgument
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.EntityAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityPropertiesObjectArgument
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathBoundProperties
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathProp
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.generic.Action
import org.bukkit.util.Vector
import java.util.concurrent.ConcurrentHashMap

class ShowEntityAction : Action<Animation> {

    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("id", "example-entity", true),
        PrimitiveObjectArgument("entity-type", "zombie", true),
        EntityPropertiesObjectArgument("properties", listOf(), false),
        PrimitiveObjectArgument("location-offset", "0;0;0", false),
        BoundPathObjectArgument(
            "bound-paths",
            { _ -> ConcurrentHashMap() },
            false
        )
    )

    override fun execute(binder: Animation, args: ObjectArguments, textUpdater: (Animation, String) -> String) {
        val id = args.string("id") { textUpdater(binder, it)} ?: return
        val type = args.string("entity-type") { textUpdater(binder, it) } ?: return
        val properties = args.typed<List<EntityProperty>>("properties") { textUpdater(binder, it) } ?: return
        val locationOffsetStrings = (args.string("location-offset") { textUpdater(binder, it)} ?: "").split(";")

        val locationOffsetVector = Vector(
            locationOffsetStrings.getOrNull(0)?.toDoubleOrNull() ?: 0.0,
            locationOffsetStrings.getOrNull(1)?.toDoubleOrNull() ?: 0.0,
            locationOffsetStrings.getOrNull(2)?.toDoubleOrNull() ?: 0.0,
        )
        val locationOffsetYawPitch =
            (locationOffsetStrings.getOrNull(3)?.toFloatOrNull() ?: 0.0f) to (locationOffsetStrings.getOrNull(4)
                ?.toFloatOrNull() ?: 0.0f)

        val boundPropertiesFactory =
            args.any("bound-paths") as ((Animation) -> ConcurrentHashMap<PathProp, PathBoundProperties>)?
                ?: { _ -> ConcurrentHashMap() }

        val boundPaths = boundPropertiesFactory(binder)
        var i = 0
        val entity = EntityAnimationProp(
            binder,
            locationOffsetVector,
            ConcurrentHashMap(boundPaths.mapValues {
                i++
                it.value to i
            }),
            type,
            properties,
            locationOffsetYawPitch
        )

        for ((path, pathProperties) in boundPaths) {
            path.boundProps += entity to pathProperties
        }

        binder.props["entity:$id"] = entity
    }
}