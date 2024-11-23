package gg.aquatic.aquaticcrates.plugin.interact.action

import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.interaction.CrateInteractAction
import gg.aquatic.aquaticseries.lib.action.AbstractAction
import gg.aquatic.aquaticseries.lib.util.argument.AquaticObjectArgument
import org.bukkit.Bukkit
import java.util.function.BiFunction

class CrateOpenAction : AbstractAction<CrateInteractAction>() {

    override fun run(
        binder: CrateInteractAction,
        args: Map<String, Any?>,
        textUpdater: BiFunction<CrateInteractAction, String, String>
    ) {
        val crate = binder.crate
        val player = binder.player
        val interactedLocation = binder.interactedLocation
        Bukkit.broadcastMessage("Opening crate!")
        if (crate is OpenableCrate) {
            crate.animationManager.animationSettings.create(
                player, crate.animationManager, interactedLocation, crate.rewardManager.getRewards(player)
            )
        }
    }

    override fun arguments(): List<AquaticObjectArgument<*>> {
        return listOf()
    }
}