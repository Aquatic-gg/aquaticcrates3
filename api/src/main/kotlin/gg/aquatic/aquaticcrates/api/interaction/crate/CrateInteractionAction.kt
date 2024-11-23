package gg.aquatic.aquaticcrates.api.interaction.crate

import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate
import gg.aquatic.waves.item.AquaticItemInteractEvent
import org.bukkit.Location
import org.bukkit.entity.Player

interface CrateInteractionAction {

    fun execute(player: Player, interactType: AquaticItemInteractEvent.InteractType, interactedLocation: Location, crate: SpawnedCrate?)
}