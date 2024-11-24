package gg.aquatic.aquaticcrates.plugin.interact.action

import gg.aquatic.aquaticcrates.api.interaction.CrateInteractAction
import gg.aquatic.aquaticcrates.plugin.crate.BasicCrate
import gg.aquatic.aquaticcrates.plugin.preview.CratePreviewMenu
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
        val menu = CratePreviewMenu(binder.player,binder.crate as? BasicCrate ?: return)
        menu.open(binder.player)
    }

    override fun arguments(): List<AquaticObjectArgument<*>> {
        return listOf()
    }
}