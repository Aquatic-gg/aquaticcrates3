package gg.aquatic.aquaticcrates.plugin.interact.action

import gg.aquatic.aquaticcrates.api.interaction.CrateInteractAction
import gg.aquatic.aquaticcrates.plugin.crate.BasicCrate
import gg.aquatic.aquaticcrates.plugin.preview.CratePreviewMenu
import gg.aquatic.aquaticseries.lib.action.AbstractAction
import gg.aquatic.aquaticseries.lib.util.argument.AquaticObjectArgument
import gg.aquatic.aquaticseries.lib.util.runSync
import org.bukkit.Bukkit
import java.util.function.BiFunction

class CratePreviewAction : AbstractAction<CrateInteractAction>() {

    override fun run(
        binder: CrateInteractAction,
        args: Map<String, Any?>,
        textUpdater: BiFunction<CrateInteractAction, String, String>
    ) {
        Bukkit.broadcastMessage("Previewing crate!")
        val crate = binder.crate
        if (crate !is BasicCrate) return
        val player = binder.player

        val settings = crate.previewMenuSettings.firstOrNull() ?: return
        runSync {
            val menu = CratePreviewMenu(player, crate, settings, 0)
            menu.open()
        }
    }

    override fun arguments(): List<AquaticObjectArgument<*>> {
        return listOf()
    }
}