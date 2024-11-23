package gg.aquatic.aquaticcrates.api.interaction

import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate
import gg.aquatic.waves.item.AquaticItemInteractEvent
import org.bukkit.Location
import org.bukkit.entity.Player

abstract class InteractHandler {

    abstract fun handleInteract(player: Player, clickType: AquaticItemInteractEvent.InteractType, interactedLocation: Location, crate: SpawnedCrate?): Boolean
}