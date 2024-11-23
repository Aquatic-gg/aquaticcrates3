package gg.aquatic.aquaticcrates.plugin.interact.action

import gg.aquatic.aquaticcrates.api.interaction.CrateInteractAction
import gg.aquatic.aquaticseries.lib.action.AbstractAction
import gg.aquatic.aquaticseries.lib.util.argument.AquaticObjectArgument
import org.bukkit.Bukkit
import java.util.function.BiFunction

class CratePreviewAction: AbstractAction<CrateInteractAction>() {

    override fun run(
        binder: CrateInteractAction,
        args: Map<String, Any?>,
        textUpdater: BiFunction<CrateInteractAction, String, String>
    ) {
        Bukkit.broadcastMessage("Previewing crate!")
    }

    override fun arguments(): List<AquaticObjectArgument<*>> {
        return listOf()
    }
}