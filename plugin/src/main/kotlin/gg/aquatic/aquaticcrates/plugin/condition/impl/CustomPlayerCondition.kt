package gg.aquatic.aquaticcrates.plugin.condition.impl

import gg.aquatic.aquaticcrates.plugin.condition.Evaluation
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.generic.Condition
import org.bukkit.entity.Player

class CustomPlayerCondition : Condition<Player> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("condition", "", true)
    )

    override fun execute(
        binder: Player,
        args: ObjectArguments,
        textUpdater: (Player, String) -> String
    ): Boolean {
        val condition =
            textUpdater(
                binder,
                args.string("condition") { textUpdater(binder, it) } ?: return false)
        return Evaluation.evaluateLogicalCondition(condition)
    }
}