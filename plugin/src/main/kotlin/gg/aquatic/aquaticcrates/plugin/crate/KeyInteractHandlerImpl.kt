package gg.aquatic.aquaticcrates.plugin.crate

import gg.aquatic.aquaticcrates.api.crate.Key
import gg.aquatic.aquaticcrates.api.interaction.key.KeyInteractHandler
import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate
import gg.aquatic.aquaticcrates.api.interaction.crate.CrateInteractionAction
import gg.aquatic.waves.item.AquaticItemInteractEvent
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.EnumMap

class KeyInteractHandlerImpl(override val requiresCrateToOpen: Boolean, override val key: Key,
                             override val clickActions: EnumMap<AquaticItemInteractEvent.InteractType, CrateInteractionAction>
) : KeyInteractHandler() {
    override fun handleInteract(player: Player, interactType: AquaticItemInteractEvent.InteractType, interactedLocation: Location, crate: SpawnedCrate?): Boolean {
        player.sendMessage("You have interacted with the key!")
        val action = clickActions[interactType] ?: return false
        player.sendMessage("Action found!")
        action.execute(player, interactType, interactedLocation, crate)
        return true
    }
}