package gg.aquatic.aquaticcrates.plugin.reroll.input.interaction

import com.github.retrooper.packetevents.event.PacketReceiveEvent
import com.github.retrooper.packetevents.protocol.player.InteractionHand
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity
import gg.aquatic.aquaticcrates.api.reroll.RerollManager
import gg.aquatic.aquaticcrates.plugin.reroll.input.interaction.InteractionRerollInput.Action
import gg.aquatic.aquaticcrates.plugin.reroll.input.interaction.InteractionRerollInput.InteractionType
import gg.aquatic.waves.util.player
import org.bukkit.event.player.PlayerToggleSneakEvent
import java.util.*
import java.util.concurrent.CompletableFuture

object InteractionInputHandler {

    val awaiting = mutableMapOf<UUID, Pair<CompletableFuture<RerollManager.RerollResult>,Map<InteractionType, Action>>>()

    fun onSneak(event: PlayerToggleSneakEvent) {
        val (future, actions) = awaiting[event.player.uniqueId] ?: return
        val action = actions[InteractionType.SNEAK] ?: return

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
    }

    fun onInteract(event: PacketReceiveEvent, packet: WrapperPlayClientInteractEntity) {
        val player = event.player() ?: return
        if (packet.hand == InteractionHand.OFF_HAND) return
        if (packet.action == WrapperPlayClientInteractEntity.InteractAction.INTERACT_AT) return
        val (future, actions) = awaiting[player.uniqueId] ?: return
        val type = if (packet.action == WrapperPlayClientInteractEntity.InteractAction.INTERACT) {
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