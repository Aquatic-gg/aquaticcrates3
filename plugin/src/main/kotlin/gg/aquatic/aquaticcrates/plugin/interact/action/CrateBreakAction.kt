package gg.aquatic.aquaticcrates.plugin.interact.action

import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import gg.aquatic.aquaticcrates.api.interaction.CrateInteractAction
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.generic.ExecutableObject

class CrateBreakAction: AbstractAction<CrateInteractAction>() {

    override val arguments: List<AquaticObjectArgument<*>> = listOf()

    override fun execute(
        binder: CrateInteractAction,
        args: Map<String, Any?>,
        textUpdater: (CrateInteractAction, String) -> String
    ) {
        val spawnedCrate = binder.spawnedCrate ?: return
        for (spawnedInteractable in spawnedCrate.spawnedInteractables) {
            spawnedInteractable.destroy()
        }
        CrateHandler.spawned -= spawnedCrate.location
    }
}