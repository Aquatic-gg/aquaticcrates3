package gg.aquatic.aquaticcrates.plugin.animation.action.entity

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.EntityAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityPropertiesObjectArgument
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import java.util.concurrent.CompletableFuture.runAsync

class UpdateEntityPropertiesAction : AbstractAction<Animation>() {

    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("id", "example-entity", true),
        EntityPropertiesObjectArgument("properties", listOf(), true),
    )

    override fun execute(binder: Animation, args: Map<String, Any?>, textUpdater: (Animation, String) -> String) {
        val id = args["id"] as String
        val properties = args["properties"] as List<EntityProperty>

        val entityProp = binder.props["entity:$id"] as? EntityAnimationProp? ?: return

        runAsync {
            entityProp.entity.updateEntity {
                for (property in properties) {
                    property.apply(this, entityProp)
                }
            }
        }
    }
}