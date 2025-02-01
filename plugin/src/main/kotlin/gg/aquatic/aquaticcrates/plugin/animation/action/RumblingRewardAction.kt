package gg.aquatic.aquaticcrates.plugin.animation.action

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationActions
import gg.aquatic.aquaticcrates.api.util.ActionsArgument
import gg.aquatic.aquaticcrates.plugin.animation.prop.RumblingRewardProp
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument

class RumblingRewardAction : AbstractAction<Animation>() {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("id", "example", true),
        PrimitiveObjectArgument("rumbling-length", 0, false),
        PrimitiveObjectArgument("rumbling-period", 0, false),
        PrimitiveObjectArgument("ease-out", false, required = false),
        PrimitiveObjectArgument("reward-index", 0, false),
        ActionsArgument("rumble-actions", null, false),
        ActionsArgument("rumble-finish-actions", null, false),
    )

    override fun execute(binder: Animation, args: Map<String, Any?>, textUpdater: (Animation, String) -> String) {
        val id = args["id"] as? String ?: return
        val rumblingLength = args["rumbling-length"]?.toString()?.toIntOrNull() ?: return
        val rumblingPeriod = args["rumbling-period"]?.toString()?.toIntOrNull() ?: return
        val easeOut = args["ease-out"] as? Boolean ?: false
        val rewardIndex = args["reward-index"]?.toString()?.toIntOrNull() ?: 0
        val rumbleActions = args["rumble-actions"] as? CrateAnimationActions ?: CrateAnimationActions()
        val rumbleFinishActions = args["rumble-finish-actions"] as? CrateAnimationActions ?: CrateAnimationActions()

        if (binder !is CrateAnimation) return
        val prop = RumblingRewardProp(
            binder,
            rumblingLength,
            rumblingPeriod,
            easeOut,
            rewardIndex,
            rumbleActions,
            rumbleFinishActions
        )

        binder.extraPlaceholders["rumbling-reward-name:$id"] = { str: String ->
            str.replace(
                "%rumbling-reward-name:$id%",
                prop.currentReward?.displayName ?: ""
            )
        }
        binder.extraPlaceholders["rumbling-reward-id:$id"] = { str: String ->
            str.replace(
                "%rumbling-reward-name:$id%",
                prop.currentReward?.id ?: ""
            )
        }
        binder.extraPlaceholders["rumbling-reward-chance:$id"] = { str: String ->
            str.replace(
                "%rumbling-reward-chance:$id%",
                prop.currentReward?.chance?.toString() ?: ""
            )
        }

        binder.extraPlaceholders["rumbling-reward-rarity-name:$id"] = { str: String ->
            str.replace(
                "%rumbling-reward-rarity-name:$id%",
                prop.currentReward?.rarity?.displayName ?: ""
            )
        }
        binder.extraPlaceholders["rumbling-reward-rarity-id:$id"] = { str: String ->
            str.replace(
                "%rumbling-reward-rarity-id:$id%",
                prop.currentReward?.rarity?.rarityId ?: ""
            )
        }

        binder.props["rumbling-reward:$id"] = prop
        prop.tick()
    }
}