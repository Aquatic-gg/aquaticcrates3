package gg.aquatic.aquaticcrates.plugin.interact.action

import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import gg.aquatic.aquaticcrates.api.interaction.CrateInteractAction
import gg.aquatic.aquaticseries.lib.action.AbstractAction
import gg.aquatic.aquaticseries.lib.util.argument.AquaticObjectArgument
import gg.aquatic.aquaticseries.lib.util.runLaterSync
import java.util.function.BiFunction

class CrateBreakAction: AbstractAction<CrateInteractAction>() {
    override fun run(
        binder: CrateInteractAction,
        args: Map<String, Any?>,
        textUpdater: BiFunction<CrateInteractAction, String, String>
    ) {
        val spawnedCrate = binder.spawnedCrate ?: return
        for (spawnedInteractable in spawnedCrate.spawnedInteractables) {
            spawnedInteractable.destroy()
        }
        CrateHandler.spawned -= spawnedCrate.location
    }

    override fun arguments(): List<AquaticObjectArgument<*>> {
        return listOf()
    }
}