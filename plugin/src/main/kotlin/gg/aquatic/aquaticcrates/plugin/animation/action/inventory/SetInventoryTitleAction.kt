package gg.aquatic.aquaticcrates.plugin.animation.action.inventory

import gg.aquatic.aquaticcrates.plugin.animation.prop.inventory.InventoryAnimationProp
import gg.aquatic.waves.scenario.PlayerScenario
import gg.aquatic.waves.util.action.RegisterAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.generic.Action
import gg.aquatic.waves.util.toMMComponent
import net.kyori.adventure.key.Key

@RegisterAction("set-inventory-title")
class SetInventoryTitleAction: Action<PlayerScenario> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("title", "Example", true)
    )

    override fun execute(
        binder: PlayerScenario,
        args: ObjectArguments,
        textUpdater: (PlayerScenario, String) -> String
    ) {
        val inventory = (binder.props[Key.key("inventory")] ?: return) as? InventoryAnimationProp ?: return
        val title = args.string("title") { textUpdater(binder, it) } ?: return
        inventory.menu.title = binder.updatePlaceholders(title).toMMComponent()
    }
}