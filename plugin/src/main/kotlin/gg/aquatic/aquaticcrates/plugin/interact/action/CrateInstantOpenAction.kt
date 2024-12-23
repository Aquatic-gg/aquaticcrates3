package gg.aquatic.aquaticcrates.plugin.interact.action

import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.interaction.CrateInteractAction
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.generic.ExecutableObject
import org.bukkit.Bukkit

class CrateInstantOpenAction: AbstractAction<CrateInteractAction>() {

    override val arguments: List<AquaticObjectArgument<*>> = listOf()

    override fun execute(
        binder: CrateInteractAction,
        args: Map<String, Any?>,
        textUpdater: (CrateInteractAction, String) -> String
    ) {
        Bukkit.broadcastMessage("Instant Opening crate!")
        val crate = binder.crate
        val player = binder.player
        if (crate is OpenableCrate) {
            crate.tryInstantOpen(player, binder.interactedLocation, binder.spawnedCrate)
        }
    }

}