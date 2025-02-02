package gg.aquatic.aquaticcrates.plugin.animation.action

import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.api.util.VectorArgument
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import org.bukkit.util.Vector

class PushPlayerAction : AbstractAction<PlayerBoundAnimation>() {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        VectorArgument("velocity", null, false),
        PrimitiveObjectArgument("power", "double", true)
    )

    override fun execute(
        binder: PlayerBoundAnimation,
        args: ObjectArguments,
        textUpdater: (PlayerBoundAnimation, String) -> String
    ) {
        val power = args.double("power") { textUpdater(binder, it) } ?: 1.0
        val velocity = args.vector("velocity") { textUpdater(binder, it) }
        val vector = velocity?.multiply(power) ?: binder.player.location.clone()
            .subtract(binder.baseLocation).toVector().normalize()
            .multiply(power)

        binder.player.velocity = vector
    }
}