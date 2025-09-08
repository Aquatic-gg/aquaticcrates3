package gg.aquatic.aquaticcrates.plugin.animation.action.entity

import gg.aquatic.waves.scenario.Scenario
import gg.aquatic.waves.scenario.prop.Passenger
import gg.aquatic.waves.scenario.prop.Seatable
import gg.aquatic.waves.util.action.RegisterAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.generic.Action
import net.kyori.adventure.key.Key

@RegisterAction("add-passenger")
class AddPassengerAction: Action<Scenario> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("passenger", "example", true),
        PrimitiveObjectArgument("seat", "entity:example", true)
    )

    override fun execute(binder: Scenario, args: ObjectArguments, textUpdater: (Scenario, String) -> String) {
        val passengerId = args.string("passenger") { textUpdater(binder, it) } ?: return
        val passenger = binder.prop<Passenger>(passengerId) ?: return

        val seatId = args.string("seat") { textUpdater(binder, it) } ?: return
        val seat = binder.props[Key.key(seatId)] as? Seatable ?: return

        seat.addPassenger(passenger)
    }
}