package gg.aquatic.aquaticcrates.api.crate

import gg.aquatic.aquaticcrates.api.hologram.AquaticHologramSettings
import gg.aquatic.aquaticcrates.api.util.ACGlobalAudience
import gg.aquatic.waves.item.AquaticItemInteractEvent
import gg.aquatic.waves.util.audience.GlobalAudience
import gg.aquatic.waves.util.updatePAPIPlaceholders
import org.bukkit.Bukkit
import org.bukkit.Location

class SpawnedCrate(
    val crate: Crate,
    val location: Location
) {

    val hologram = crate.hologramSettings.create(location)

    init {
        Bukkit.getConsoleSender()
            .sendMessage("A Crate has been spawned at ${location.x}, ${location.y}, ${location.z} in world ${location.world?.name}")
        Bukkit.getConsoleSender()
            .sendMessage("  Hologram lines: ${(crate.hologramSettings as AquaticHologramSettings).lines.size}")

        hologram.spawn(ACGlobalAudience()) { p, str ->
            str.updatePAPIPlaceholders(p)
        }
    }

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

    fun destroy() {
        for (spawnedInteractable in spawnedInteractables) {
            spawnedInteractable.destroy()
        }
        hologram.despawn()
    }
}