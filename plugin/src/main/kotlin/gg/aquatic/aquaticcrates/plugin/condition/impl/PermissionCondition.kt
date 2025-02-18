package gg.aquatic.aquaticcrates.plugin.condition.impl

import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.generic.Condition
import org.bukkit.entity.Player

class PermissionCondition: Condition<Player> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("permission", "", true)
    )

    override fun execute(binder: Player, args: ObjectArguments, textUpdater: (Player, String) -> String): Boolean {
        val permission = args.string("permission") { textUpdater(binder, it) } ?: return false
        return binder.hasPermission(permission)
    }
}