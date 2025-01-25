package gg.aquatic.aquaticcrates.plugin.animation.action.entity

import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.SpawnedRewardAnimationProp
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import org.bukkit.util.Vector

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
    )

    override fun execute(
        binder: PlayerBoundAnimation,
        args: Map<String, Any?>,
        textUpdater: (PlayerBoundAnimation, String) -> String
    ) {
        val id = args["id"] as? String ?: "example"
        val rumblingLength = args["rumbling-length"]?.toString()?.toIntOrNull() ?: 0
        val rumblingPeriod = args["rumbling-period"]?.toString()?.toIntOrNull() ?: 0
        val stay = args["stay"] as? Int ?: 200
        val offset = (args["offset"] as? String ?: "0;0;0").split(";")
        val velocity = (args["velocity"] as? String ?: "0;0;0").split(";")
        val gravity = args["gravity"] as? Boolean ?: true
        val easeOut = args["ease-out"] as? Boolean ?: false
        val rewardIndex = args["reward-index"] as? Int ?: 0

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
    }
}