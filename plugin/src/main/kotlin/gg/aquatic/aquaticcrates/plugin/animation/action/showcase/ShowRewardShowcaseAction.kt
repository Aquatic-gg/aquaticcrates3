package gg.aquatic.aquaticcrates.plugin.animation.action.showcase

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.plugin.animation.prop.showcase.RewardShowcaseAnimationProp
import gg.aquatic.waves.util.action.RegisterAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.generic.Action
import org.bukkit.util.Vector

@RegisterAction("show-reward-showcase")
class ShowRewardShowcaseAction: Action<Animation> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("id", "example", true),
        PrimitiveObjectArgument("location-offset","0;0;0", false)
    )

    override fun execute(
        binder: Animation,
        args: ObjectArguments,
        textUpdater: (Animation, String) -> String
    ) {
        val id = args.string("id") { textUpdater(binder, it) } ?: return

        val locationOffsetStrings = (args.string("location-offset") { textUpdater(binder, it)} ?: "").split(";")

        val locationOffsetVector = Vector(
            locationOffsetStrings.getOrNull(0)?.toDoubleOrNull() ?: 0.0,
            locationOffsetStrings.getOrNull(1)?.toDoubleOrNull() ?: 0.0,
            locationOffsetStrings.getOrNull(2)?.toDoubleOrNull() ?: 0.0,
        )
        val locationOffsetYawPitch =
            (locationOffsetStrings.getOrNull(3)?.toFloatOrNull() ?: 0.0f) to (locationOffsetStrings.getOrNull(4)
                ?.toFloatOrNull() ?: 0.0f)

        val prop = RewardShowcaseAnimationProp(binder, locationOffsetVector to locationOffsetYawPitch)
        binder.props["reward-showcase:$id"]?.onAnimationEnd()
        binder.props["reward-showcase:$id"] = prop
    }
}