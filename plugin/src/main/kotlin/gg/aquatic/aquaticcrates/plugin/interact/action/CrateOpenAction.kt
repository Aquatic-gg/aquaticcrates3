package gg.aquatic.aquaticcrates.plugin.interact.action

import gg.aquatic.aquaticcrates.api.interaction.CrateInteractAction
import gg.aquatic.aquaticcrates.plugin.crate.BasicCrate
import gg.aquatic.aquaticseries.lib.action.AbstractAction
import gg.aquatic.aquaticseries.lib.util.argument.AquaticObjectArgument
import java.util.function.BiFunction

class CrateOpenAction : AbstractAction<CrateInteractAction>() {

    override fun run(
        binder: CrateInteractAction,
        args: Map<String, Any?>,
        textUpdater: BiFunction<CrateInteractAction, String, String>
    ) {
        val crate = binder.crate
        if (crate is BasicCrate) {
            crate.open(binder.player, binder.interactedLocation, binder.spawnedCrate)
        }
    }

    override fun arguments(): List<AquaticObjectArgument<*>> {
        return listOf()
    }
}