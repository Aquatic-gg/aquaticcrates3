package gg.aquatic.aquaticcrates.plugin.animation.action.entity

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.plugin.animation.prop.Seatable
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.EntityAnimationProp
import gg.aquatic.waves.util.action.RegisterAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.generic.Action

@RegisterAction("add-passenger")
class AddPassengerAction: Action<Animation> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("passenger", "example", true),
        PrimitiveObjectArgument("seat", "entity:example", true)
    )

    override fun execute(binder: Animation, args: ObjectArguments, textUpdater: (Animation, String) -> String) {
        val passengerId = args.string("passenger") { textUpdater(binder, it) } ?: return
        val passenger = binder.props[passengerId] as? EntityAnimationProp ?: return

        val seatId = args.string("seat") { textUpdater(binder, it) } ?: return
        val seat = binder.props[seatId] as? Seatable ?: return

        seat.addPassenger(passenger)
    }
}