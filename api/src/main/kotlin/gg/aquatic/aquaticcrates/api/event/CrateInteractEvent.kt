package gg.aquatic.aquaticcrates.api.event

import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate
import gg.aquatic.waves.api.event.AquaticEvent
import gg.aquatic.waves.item.AquaticItemInteractEvent
import org.bukkit.Location

class CrateInteractEvent(
    val player: org.bukkit.entity.Player,
    val placedCrate: SpawnedCrate?,
    val interactType: AquaticItemInteractEvent.InteractType,
    val interactedLocation: Location,
): AquaticEvent() {

    val isKeyInteraction: Boolean
        get() {
            return placedCrate == null
        }

}