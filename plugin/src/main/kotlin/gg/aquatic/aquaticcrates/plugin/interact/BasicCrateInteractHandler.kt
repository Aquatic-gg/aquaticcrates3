package gg.aquatic.aquaticcrates.plugin.interact

import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.interaction.crate.CrateInteractHandler
import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate
import gg.aquatic.aquaticcrates.api.interaction.CrateInteractAction
import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import gg.aquatic.waves.item.AquaticItemInteractEvent
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.EnumMap

class BasicCrateInteractHandler(
    override val crate: Crate,
    override val clickActions: EnumMap<AquaticItemInteractEvent.InteractType, ConfiguredAction<CrateInteractAction>>
) : CrateInteractHandler() {

    override fun handleInteract(
        player: Player,
        interactType: AquaticItemInteractEvent.InteractType,
        interactedLocation: Location,
        crate: SpawnedCrate?
    ): Boolean {
        player.sendMessage("You have interacted the crate!")
        val action = clickActions[interactType] ?: return false
        player.sendMessage("Action found!")
        action.run(CrateInteractAction(this.crate, player, interactType, interactedLocation, crate)) {
            _, str -> str
        }
        return true
    }

}