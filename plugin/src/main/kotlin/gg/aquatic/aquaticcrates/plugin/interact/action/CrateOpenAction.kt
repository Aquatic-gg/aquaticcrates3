package gg.aquatic.aquaticcrates.plugin.interact.action

import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.interaction.CrateInteractAction
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.generic.ExecutableObject

class CrateOpenAction : AbstractAction<CrateInteractAction>() {

    override val arguments: List<AquaticObjectArgument<*>> = listOf()

    override fun execute(
        binder: CrateInteractAction,
        args: Map<String, Any?>,
        textUpdater: (CrateInteractAction, String) -> String
    ) {
        val crate = binder.crate
        if (crate is OpenableCrate) {
            crate.tryOpen(binder.player, binder.interactedLocation, binder.spawnedCrate)
        }
    }
}