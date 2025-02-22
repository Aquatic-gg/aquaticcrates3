package gg.aquatic.aquaticcrates.plugin.condition.impl

import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.generic.Condition
import org.bukkit.entity.Player
import java.time.LocalDate
import java.time.temporal.ChronoField

class DayRepeatCondition: Condition<Player> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("day", 2, true)
    )

    override fun execute(binder: Player, args: ObjectArguments, textUpdater: (Player, String) -> String): Boolean {
        val day = args.int("day") { textUpdater(binder, it) } ?: return false
        return getCurrentDay() % day == 0
    }

    private fun getCurrentDay(): Int {
        val currentDate = LocalDate.now()
        return currentDate.get(ChronoField.DAY_OF_YEAR)  // Obtain the current day of the year (1-365/366)
    }
}