package gg.aquatic.aquaticcrates.api.interaction

import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate
import gg.aquatic.waves.item.AquaticItemInteractEvent
import org.bukkit.Location
import org.bukkit.entity.Player

class CrateInteractAction(
    val crate: Crate,
    val player: Player,
    val interactType: AquaticItemInteractEvent.InteractType,
    val interactedLocation: Location,
    val spawnedCrate: SpawnedCrate?
) {
}