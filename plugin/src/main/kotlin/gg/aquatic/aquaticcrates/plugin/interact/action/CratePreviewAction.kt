package gg.aquatic.aquaticcrates.plugin.interact.action

import gg.aquatic.aquaticcrates.api.interaction.CrateInteractAction
import gg.aquatic.aquaticcrates.plugin.crate.BasicCrate
import gg.aquatic.waves.util.action.RegisterAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.generic.Action

@RegisterAction("preview-crate")
class CratePreviewAction : Action<CrateInteractAction> {

    override val arguments: List<AquaticObjectArgument<*>> = listOf()

    override fun execute(
        binder: CrateInteractAction,
        args: ObjectArguments,
        textUpdater: (CrateInteractAction, String) -> String
    ) {
        val crate = binder.crate
        if (crate !is BasicCrate) return
        val player = binder.player

        crate.openPreview(player, binder.spawnedCrate)
    }
}