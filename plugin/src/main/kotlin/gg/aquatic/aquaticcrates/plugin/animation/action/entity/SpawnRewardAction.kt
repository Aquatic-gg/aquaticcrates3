package gg.aquatic.aquaticcrates.plugin.animation.action.entity

import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationActions
import gg.aquatic.aquaticcrates.api.util.ActionsArgument
import gg.aquatic.aquaticcrates.plugin.animation.prop.RumblingRewardProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.EntityAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl.EntityDataProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.timer.LaterActionsAnimationProp
import gg.aquatic.waves.packetevents.EntityDataBuilder
import gg.aquatic.waves.packetevents.type.ItemEntityDataBuilder
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.concurrent.ConcurrentHashMap

class SpawnRewardAction : AbstractAction<PlayerBoundAnimation>() {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("id", "example", true),
        PrimitiveObjectArgument("rumbling-length", 0, false),
        PrimitiveObjectArgument("rumbling-period", 0, false),
        PrimitiveObjectArgument("stay", 200, true),
        PrimitiveObjectArgument("offset", "0;0;0", false),
        PrimitiveObjectArgument("velocity", "0;0;0", false),
        PrimitiveObjectArgument("gravity", true, required = false),
        PrimitiveObjectArgument("ease-out", false, required = false),
        PrimitiveObjectArgument("reward-index", 0, false),
        ActionsArgument("rumble-actions", null, true),
        ActionsArgument("rumble-finish-actions", null, true),
    )

    override fun execute(
        binder: PlayerBoundAnimation,
        args: Map<String, Any?>,
        textUpdater: (PlayerBoundAnimation, String) -> String
    ) {
        try {
            val id = args["id"] as? String ?: "example"
            val rumblingLength = args["rumbling-length"]?.toString()?.toIntOrNull() ?: 0
            val rumblingPeriod = args["rumbling-period"]?.toString()?.toIntOrNull() ?: 0
            val stay = args["stay"] as? Int ?: 200
            val offset = (args["offset"] as? String ?: "0;0;0").split(";")
            val velocity = (args["velocity"] as? String ?: "0;0;0").split(";")
            val gravity = args["gravity"] as? Boolean ?: true
            val easeOut = args["ease-out"] as? Boolean ?: false
            val rewardIndex = args["reward-index"] as? Int ?: 0
            val rumbleActions = args["rumble-actions"] as? CrateAnimationActions ?: CrateAnimationActions(
                mutableListOf(),
                mutableListOf()
            )
            val rumbleFinishActions = args["rumble-finish-actions"] as? CrateAnimationActions ?: CrateAnimationActions(
                mutableListOf(),
                mutableListOf()
            )

            val offsetVector = Vector(
                offset.getOrNull(0)?.toDoubleOrNull() ?: 0.0,
                offset.getOrNull(1)?.toDoubleOrNull() ?: 0.0,
                offset.getOrNull(2)?.toDoubleOrNull() ?: 0.0
            )
            val velocityVector = Vector(
                velocity.getOrNull(0)?.toDoubleOrNull() ?: 0.0,
                velocity.getOrNull(1)?.toDoubleOrNull() ?: 0.0,
                velocity.getOrNull(2)?.toDoubleOrNull() ?: 0.0
            )

            if (binder !is CrateAnimation) return

            val rumblingRewardProp = RumblingRewardProp(
                binder,
                rumblingLength,
                rumblingPeriod,
                easeOut,
                rewardIndex,
                rumbleActions,
                rumbleFinishActions
            )

            val property = object : gg.aquatic.waves.interactable.settings.entityproperty.EntityProperty {
                override fun apply(builder: EntityDataBuilder, updater: (String) -> String) {
                    builder.hasNoGravity(!gravity)
                    val item = rumblingRewardProp.currentReward?.item?.getItem() ?: ItemStack(Material.AIR)
                    (builder as ItemEntityDataBuilder).setItem(
                        item
                    )
                }
            }

            val entityProperties = listOf(
                EntityDataProperty(
                    listOf(
                        property
                    )
                )
            )

            val rewardItemProp = EntityAnimationProp(
                binder,
                offsetVector.clone().add(Vector(0, 1, 0)),
                ConcurrentHashMap(),
                "ITEM",
                entityProperties
            )
            binder.props["entity:reward-item-$id"] = rewardItemProp
            rewardItemProp.throwObject(velocityVector)

            val updateAction = ConfiguredExecutableObject(
                UpdateEntityPropertiesAction(),
                hashMapOf(
                    "id" to "reward-item-$id",
                    "properties" to entityProperties
                )
            )
            rumbleActions.animationActions.add(updateAction)

            binder.props["rumbling-reward:$id"] = rumblingRewardProp

            val hideAction = ConfiguredExecutableObject(
                HideEntityAction(),
                hashMapOf(
                    "id" to "reward-item-$id"
                )
            )
            val timedAction = LaterActionsAnimationProp(
                binder,
                CrateAnimationActions(
                    mutableListOf(
                        hideAction
                    ),
                    mutableListOf()
                ),
                stay
            )
            binder.props["timed-action:$id"] = timedAction

            /*
        val prop = SpawnedRewardAnimationProp(
            binder,
            rumblingLength,
            rumblingPeriod,
            stay,
            offsetVector,
            velocityVector,
            gravity,
            easeOut,
            rewardIndex
        )
        binder.props["reward:$id"] = prop

         */
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}