package gg.aquatic.aquaticcrates.plugin.interact.action

import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.interaction.CrateInteractAction
import gg.aquatic.aquaticcrates.plugin.crate.BasicCrate
import gg.aquatic.aquaticseries.lib.action.AbstractAction
import gg.aquatic.aquaticseries.lib.util.argument.AquaticObjectArgument
import org.bukkit.Bukkit
import java.util.function.BiFunction

class CrateInstantOpenAction: AbstractAction<CrateInteractAction>() {
    override fun run(
        binder: CrateInteractAction,
        args: Map<String, Any?>,
        textUpdater: BiFunction<CrateInteractAction, String, String>
    ) {
        Bukkit.broadcastMessage("Instant Opening crate!")
        val crate = binder.crate
        val player = binder.player
        if (crate is OpenableCrate) {
            crate.tryInstantOpen(player, binder.interactedLocation, binder.spawnedCrate)
        }
    }

    override fun arguments(): List<AquaticObjectArgument<*>> {
        return listOf()
    }

}