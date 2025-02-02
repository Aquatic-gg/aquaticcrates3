package gg.aquatic.aquaticcrates.plugin.interact.action

import gg.aquatic.aquaticcrates.api.interaction.CrateInteractAction
import gg.aquatic.aquaticcrates.plugin.crate.BasicCrate
import gg.aquatic.aquaticcrates.plugin.preview.CratePreviewMenu
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments

class CratePreviewAction : AbstractAction<CrateInteractAction>() {

    override val arguments: List<AquaticObjectArgument<*>> = listOf()

    override fun execute(
        binder: CrateInteractAction,
        args: ObjectArguments,
        textUpdater: (CrateInteractAction, String) -> String
    ) {
        val crate = binder.crate
        if (crate !is BasicCrate) return
        val player = binder.player

        val settings = crate.previewMenuSettings.firstOrNull() ?: return
        val menu = CratePreviewMenu(player, crate, settings, 0)
        menu.open()
    }
}