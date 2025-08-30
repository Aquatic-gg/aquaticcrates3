package gg.aquatic.aquaticcrates.plugin.animation.condition

import gg.aquatic.aquaticcrates.plugin.condition.Evaluation
import gg.aquatic.waves.scenario.Scenario
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.generic.Condition

class CustomCondition : Condition<Scenario> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("condition", "", true)
    )

    override fun execute(
        binder: Scenario,
        args: ObjectArguments,
        textUpdater: (Scenario, String) -> String
    ): Boolean {
        val condition = binder.updatePlaceholders(
            textUpdater(
                binder,
                args.string("condition") { textUpdater(binder, it) } ?: return false))
        return Evaluation.evaluateLogicalCondition(condition)
    }
}