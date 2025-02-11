package gg.aquatic.aquaticcrates.api.crate

import gg.aquatic.aquaticcrates.api.util.ACGlobalAudience
import gg.aquatic.waves.item.AquaticItemInteractEvent
import gg.aquatic.waves.util.updatePAPIPlaceholders
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player

class SpawnedCrate(
    val crate: Crate,
    val location: Location
) {

    val audience = ACGlobalAudience()

    val hologram = crate.hologramSettings.create(location)

    val spawnedInteractables = crate.interactables.map {
        it.build(location, audience) { e ->
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

    init {
        hologram.spawn(audience) { p, str ->
            str.updatePAPIPlaceholders(p)
        }

        if (crate is OpenableCrate) {
            crate.animationManager.playNewIdleAnimation(this)
        }
    }

    fun destroy() {
        for (spawnedInteractable in spawnedInteractables) {
            spawnedInteractable.destroy()
        }
        hologram.despawn()
    }

    fun forceHide(player: Player, hide: Boolean) {
        if (hide) {
            audience.hiddenFrom.add(player)
            spawnedInteractables.forEach { it.removeViewer(player) }
            return
        }
        audience.hiddenFrom.remove(player)
        spawnedInteractables.forEach { it.addViewer(player) }
    }

    fun forceHide(hide: Boolean) {
        if (hide) {
            val viewers = audience.uuids.mapNotNull { Bukkit.getPlayer(it) }
            audience.hidden = true
            for (spawnedInteractable in spawnedInteractables) {
                for (op in viewers) {
                    spawnedInteractable.removeViewer(op)
                }
                spawnedInteractable.viewers.clear()
                spawnedInteractable.updateViewers()
            }
            return
        }

        audience.hidden = false
        val viewers = audience.uuids.mapNotNull { Bukkit.getPlayer(it) }
        for (spawnedInteractable in spawnedInteractables) {
            spawnedInteractable.viewers += viewers
            spawnedInteractable.updateViewers()
        }
    }
}