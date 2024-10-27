package gg.aquatic.aquaticcrates.plugin.animation.action.entity

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.EntityAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityPropertiesObjectArgument
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.aquaticseries.lib.AquaticSeriesLib
import gg.aquatic.aquaticseries.lib.action.AbstractAction
import gg.aquatic.aquaticseries.lib.util.argument.AquaticObjectArgument
import gg.aquatic.aquaticseries.lib.util.argument.impl.PrimitiveObjectArgument
import java.util.function.BiFunction

class UpdateEntityPropertiesAction : AbstractAction<Animation>() {
    override fun run(binder: Animation, args: Map<String, Any?>, textUpdater: BiFunction<Animation, String, String>) {
        val id = args["id"] as String
        val properties = args["properties"] as List<EntityProperty>

        val entityProp = binder.props["entity:$id"] as? EntityAnimationProp? ?: return
        AquaticSeriesLib.INSTANCE.nmsAdapter!!.updateEntity(
            entityProp.entityId, { entity ->
                properties.forEach { it.apply(entity, entityProp) }
            },
            binder.audience
        )
    }

    override fun arguments(): List<AquaticObjectArgument<*>> {
        return listOf(
            PrimitiveObjectArgument("id", "example-entity", true),
            EntityPropertiesObjectArgument("properties", listOf(), true),
        )
    }
}