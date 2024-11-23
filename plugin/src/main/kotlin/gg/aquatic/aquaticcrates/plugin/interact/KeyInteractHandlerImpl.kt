package gg.aquatic.aquaticcrates.plugin.interact

import gg.aquatic.aquaticcrates.api.crate.Key
import gg.aquatic.aquaticcrates.api.interaction.key.KeyInteractHandler
import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate
import gg.aquatic.aquaticcrates.api.interaction.CrateInteractAction
import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import gg.aquatic.waves.item.AquaticItemInteractEvent
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.EnumMap

class KeyInteractHandlerImpl(
    override val requiresCrateToOpen: Boolean, override val key: Key,
    override val clickActions: EnumMap<AquaticItemInteractEvent.InteractType, ConfiguredAction<CrateInteractAction>>
) : KeyInteractHandler() {
    override fun handleInteract(
        player: Player,
        interactType: AquaticItemInteractEvent.InteractType,
        interactedLocation: Location,
        crate: SpawnedCrate?
    ): Boolean {
        player.sendMessage("You have interacted with the key!")
        val action = clickActions[interactType] ?: return false
        player.sendMessage("Action found!")

        action.run(CrateInteractAction(this.key.crate, player, interactType, interactedLocation, crate)) {
                _, str -> str
        }
        return true
    }
}