package gg.aquatic.aquaticcrates.api.crate

import gg.aquatic.waves.item.AquaticItemInteractEvent
import gg.aquatic.waves.util.audience.GlobalAudience
import org.bukkit.Location

class SpawnedCrate(
    val crate: Crate,
    val location: Location
) {

    val spawnedInteractables = crate.interactables.map {
        it.build(location, GlobalAudience()) { e ->
            val clickType = if (e.isLeft) {
                if (e.player.isSneaking) {
                    AquaticItemInteractEvent.InteractType.SHIFT_LEFT
                } else {
                    AquaticItemInteractEvent.InteractType.LEFT
                }
            } else {
                if (e.player.isSneaking) {
                    AquaticItemInteractEvent.InteractType.SHIFT_RIGHT
                } else {
                    AquaticItemInteractEvent.InteractType.RIGHT
                }
            }
            crate.interactHandler.handleInteract(e.player, clickType, location, this)
        }
    }
}