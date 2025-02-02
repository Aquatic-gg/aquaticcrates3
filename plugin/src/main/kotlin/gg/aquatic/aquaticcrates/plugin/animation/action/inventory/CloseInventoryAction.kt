package gg.aquatic.aquaticcrates.plugin.animation.action.inventory

import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.plugin.animation.prop.inventory.InventoryAnimationProp
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments

class CloseInventoryAction: AbstractAction<PlayerBoundAnimation>() {
    override val arguments: List<AquaticObjectArgument<*>> = listOf()

    override fun execute(
        binder: PlayerBoundAnimation,
        args: ObjectArguments,
        textUpdater: (PlayerBoundAnimation, String) -> String
    ) {
        val menu = (binder.props["inventory"] ?: return) as? InventoryAnimationProp ?: return
        binder.props.remove("inventory")
        menu.menu.close()
    }
}