package gg.aquatic.aquaticcrates.plugin.condition.impl

import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.generic.Condition
import org.bukkit.entity.Player
import java.time.LocalDate
import java.time.temporal.IsoFields

class WeekRepeatCondition: Condition<Player> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("week", 2, true)
    )

    override fun execute(binder: Player, args: ObjectArguments, textUpdater: (Player, String) -> String): Boolean {
        val week = args.int("week") { textUpdater(binder, it) } ?: return false
        return getCurrentWeek() % week == 0
    }

    private fun getCurrentWeek(): Int {
        val currentDate = LocalDate.now()
        return currentDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)
    }

}