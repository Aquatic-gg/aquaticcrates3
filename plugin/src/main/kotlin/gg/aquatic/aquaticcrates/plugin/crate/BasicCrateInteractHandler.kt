package gg.aquatic.aquaticcrates.plugin.crate

import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.interaction.crate.CrateInteractHandler
import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate
import gg.aquatic.aquaticcrates.api.interaction.crate.CrateInteractionAction
import gg.aquatic.waves.item.AquaticItemInteractEvent
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.EnumMap

class BasicCrateInteractHandler(override val crate: Crate,
                                override val clickActions: EnumMap<AquaticItemInteractEvent.InteractType, CrateInteractionAction>
) : CrateInteractHandler() {

    override fun handleInteract(player: Player, interactType: AquaticItemInteractEvent.InteractType, interactedLocation: Location, crate: SpawnedCrate?): Boolean {
        player.sendMessage("You have interacted the crate!")
        val action = clickActions[interactType] ?: return false
        player.sendMessage("Action found!")
        action.execute(player, interactType, interactedLocation, crate)
        return true
    }

}