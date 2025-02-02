package gg.aquatic.aquaticcrates.plugin.interact.action

import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.interaction.CrateInteractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.generic.Action

class CrateInstantOpenAction: Action<CrateInteractAction> {

    override val arguments: List<AquaticObjectArgument<*>> = listOf()

    override fun execute(
        binder: CrateInteractAction,
        args: ObjectArguments,
        textUpdater: (CrateInteractAction, String) -> String
    ) {
        val crate = binder.crate
        val player = binder.player
        if (crate is OpenableCrate) {
            crate.tryInstantOpen(player, binder.interactedLocation, binder.spawnedCrate)
        }
    }

}