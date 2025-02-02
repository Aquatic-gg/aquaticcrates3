package gg.aquatic.aquaticcrates.plugin.interact.action

import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import gg.aquatic.aquaticcrates.api.interaction.CrateInteractAction
import gg.aquatic.aquaticcrates.plugin.CratesPlugin
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.generic.Action

class CrateBreakAction: Action<CrateInteractAction> {

    override val arguments: List<AquaticObjectArgument<*>> = listOf()

    override fun execute(
        binder: CrateInteractAction,
        args: ObjectArguments,
        textUpdater: (CrateInteractAction, String) -> String
    ) {
        val spawnedCrate = binder.spawnedCrate ?: return
        for (spawnedInteractable in spawnedCrate.spawnedInteractables) {
            spawnedInteractable.destroy()
        }
        CrateHandler.spawned -= spawnedCrate.location
        CrateHandler.saveSpawnedCrates(CratesPlugin.spawnedCratesConfig)
    }
}