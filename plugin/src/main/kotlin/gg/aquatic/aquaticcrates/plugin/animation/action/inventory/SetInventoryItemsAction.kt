package gg.aquatic.aquaticcrates.plugin.animation.action.inventory

import gg.aquatic.aquaticcrates.api.util.animationitem.ArgumentItem
import gg.aquatic.aquaticcrates.plugin.animation.action.inventory.OpenInventoryAction.ItemsArgument
import gg.aquatic.aquaticcrates.plugin.animation.prop.inventory.InventoryAnimationProp
import gg.aquatic.waves.menu.component.Button
import gg.aquatic.waves.scenario.PlayerScenario
import gg.aquatic.waves.util.action.RegisterAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.generic.Action
import net.kyori.adventure.key.Key

@RegisterAction("set-inventory-items")
class SetInventoryItemsAction: Action<PlayerScenario> {

    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        ItemsArgument("items", mapOf(), true)
    )

    override fun execute(
        binder: PlayerScenario,
        args: ObjectArguments,
        textUpdater: (PlayerScenario, String) -> String
    ) {
        val menu = (binder.props[Key.key("inventory")] ?: return) as InventoryAnimationProp
        val items = args.typed<Map<Int, ArgumentItem>>("items") ?: return
        for ((slot, item) in items) {

            val component = Button(
                "slot_$slot",
                item.getActualItem(binder).getItem(),
                listOf(slot),
                1,
                1,
                null,
                { _ -> true },
                { str, _ -> binder.updatePlaceholders(str) },
                { _ -> }
            )
            menu.menu.components[component.id] = component
            menu.menu.updateComponent(component)
        }
    }

}