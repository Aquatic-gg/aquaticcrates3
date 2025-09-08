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
import kotlin.math.cos
import kotlin.math.sin

@RegisterAction("show-reward-showcase")
class ShowRewardShowcaseAction: Action<CrateAnimation> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("id", "example", true),
        PrimitiveObjectArgument("location-offset","0;0;0", false),
        PrimitiveObjectArgument("velocity", "0;0;0", false),
        PrimitiveObjectArgument("pitch", 0.0, false),
        PrimitiveObjectArgument("yaw", 0.0, false),
        PrimitiveObjectArgument("power", 1.0, false),
    )

    override fun execute(
        binder: CrateAnimation,
        args: ObjectArguments,
        textUpdater: (CrateAnimation, String) -> String
    ) {
        val id = args.string("id") { textUpdater(binder, it) } ?: return
        var velocity = args.vector("velocity") { textUpdater(binder, it) }
        val power = args.double("power") { textUpdater(binder, it) } ?: 1.0

        if (velocity == null) {
            val yaw = args.double("yaw") { textUpdater(binder, it) } ?: 0.0
            val pitch = args.double("pitch") { textUpdater(binder, it) } ?: 0.0

            velocity = vectorFromYawPitch(yaw.toFloat(), pitch.toFloat())
        }

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

    fun vectorFromYawPitch(yaw: Float, pitch: Float): Vector {
        val pitchRadians = Math.toRadians(pitch.toDouble())
        val yawRadians = Math.toRadians(yaw.toDouble())

        // Calculate the components
        val x = -sin(yawRadians) * cos(pitchRadians)
        val y = -sin(pitchRadians)
        val z = cos(yawRadians) * cos(pitchRadians)

        return Vector(x, y, z)
    }
}