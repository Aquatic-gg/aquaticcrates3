package gg.aquatic.aquaticcrates.plugin.animation.action.showcase

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.plugin.animation.prop.showcase.RewardShowcaseAnimationProp
import gg.aquatic.waves.util.action.RegisterAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.generic.Action
import net.kyori.adventure.key.Key
import org.bukkit.util.Vector

@RegisterAction("show-reward-showcase")
class ShowRewardShowcaseAction: Action<CrateAnimation> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("id", "example", true),
        PrimitiveObjectArgument("location-offset","0;0;0", false),
        PrimitiveObjectArgument("velocity", "0;0;0", false),
        PrimitiveObjectArgument("power", 1.0, false),
    )

    override fun execute(
        binder: CrateAnimation,
        args: ObjectArguments,
        textUpdater: (CrateAnimation, String) -> String
    ) {
        val id = args.string("id") { textUpdater(binder, it) } ?: return
        val velocity = args.vector("velocity") { textUpdater(binder, it) } ?: return
        val power = args.double("power") { textUpdater(binder, it) } ?: 0.0

        val vector = velocity.clone().multiply(power)

        val locationOffsetStrings = (args.string("location-offset") { textUpdater(binder, it)} ?: "").split(";")

        val locationOffsetVector = Vector(
            locationOffsetStrings.getOrNull(0)?.toDoubleOrNull() ?: 0.0,
            locationOffsetStrings.getOrNull(1)?.toDoubleOrNull() ?: 0.0,
            locationOffsetStrings.getOrNull(2)?.toDoubleOrNull() ?: 0.0,
        )
        val locationOffsetYawPitch =
            (locationOffsetStrings.getOrNull(3)?.toFloatOrNull() ?: 0.0f) to (locationOffsetStrings.getOrNull(4)
                ?.toFloatOrNull() ?: 0.0f)

        val prop = RewardShowcaseAnimationProp(binder, locationOffsetVector to locationOffsetYawPitch, vector)

        val key = Key.key("reward-showcase:$id")

        binder.props[key]?.onEnd()
        binder.props[key] = prop
    }
}