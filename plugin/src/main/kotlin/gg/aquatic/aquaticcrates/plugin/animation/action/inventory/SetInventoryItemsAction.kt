package gg.aquatic.aquaticcrates.plugin.animation.action.inventory

import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.plugin.animation.action.inventory.OpenInventoryAction.ItemsArgument
import gg.aquatic.aquaticcrates.plugin.animation.prop.inventory.InventoryAnimationProp
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.menu.component.Button
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument

class SetInventoryItemsAction: AbstractAction<PlayerBoundAnimation>() {

    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        ItemsArgument("items", mapOf(), true)
    )

    override fun execute(
        binder: PlayerBoundAnimation,
        args: Map<String, Any?>,
        textUpdater: (PlayerBoundAnimation, String) -> String
    ) {
        val menu = (binder.props["inventory"] ?: return) as InventoryAnimationProp
        val items = args["items"] as? Map<Int, AquaticItem> ?: return
        for ((slot, item) in items) {

            val component = Button(
                "slot_$slot",
                item.getItem(),
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