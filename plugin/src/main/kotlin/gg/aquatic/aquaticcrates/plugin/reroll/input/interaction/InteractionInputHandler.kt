package gg.aquatic.aquaticcrates.plugin.reroll.input.interaction

import gg.aquatic.aquaticcrates.api.reroll.RerollManager
import gg.aquatic.aquaticcrates.plugin.reroll.input.interaction.InteractionRerollInput.Action
import gg.aquatic.aquaticcrates.plugin.reroll.input.interaction.InteractionRerollInput.InteractionType
import gg.aquatic.waves.api.event.packet.PacketInteractEvent
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerToggleSneakEvent
import java.util.*
import java.util.concurrent.CompletableFuture

object InteractionInputHandler {

    val awaiting = mutableMapOf<UUID, Pair<CompletableFuture<RerollManager.RerollResult>,Map<InteractionType, Action>>>()

    fun onSneak(event: PlayerToggleSneakEvent): Boolean {
        val (future, actions) = awaiting[event.player.uniqueId] ?: return false
        val action = actions[InteractionType.SNEAK] ?: return false

        when(action) {
            Action.REROLL -> {
                future.complete(RerollManager.RerollResult(true))
                awaiting.remove(event.player.uniqueId)
            }
            Action.CLAIM -> {
                future.complete(RerollManager.RerollResult(false))
                awaiting.remove(event.player.uniqueId)
            }
        }
        return true
    }

    fun onInteract(event: PacketInteractEvent) {
        val player = event.player
        if (event.isSecondary) return
        val (future, actions) = awaiting[player.uniqueId] ?: return
        val type = if (event.interactType == PacketInteractEvent.InteractType.INTERACT) {
            InteractionType.RIGHT_CLICK
        } else {
            InteractionType.LEFT_CLICK
        }
        val action = actions[type] ?: return
        when(action) {
            Action.REROLL -> {
                future.complete(RerollManager.RerollResult(true))
                awaiting.remove(player.uniqueId)
            }
            Action.CLAIM -> {
                future.complete(RerollManager.RerollResult(false))
                awaiting.remove(player.uniqueId)
            }
        }
    }

}