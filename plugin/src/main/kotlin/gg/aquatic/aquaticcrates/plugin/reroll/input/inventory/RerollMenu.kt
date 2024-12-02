package gg.aquatic.aquaticcrates.plugin.reroll.input.inventory

import gg.aquatic.aquaticcrates.api.reroll.RerollManager
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticseries.lib.betterinventory2.AquaticInventory
import gg.aquatic.aquaticseries.lib.betterinventory2.SlotSelection
import gg.aquatic.aquaticseries.lib.betterinventory2.component.ButtonComponent
import gg.aquatic.aquaticseries.lib.util.executeActions
import gg.aquatic.aquaticseries.lib.util.updatePAPIPlaceholders
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.concurrent.CompletableFuture

class RerollMenu(
    val player: Player,
    val rewards: List<Reward>,
    val settings: RerollInventorySettings,
    val future: CompletableFuture<RerollManager.RerollResult>
) : AquaticInventory(
    settings.inventorySettings.title,
    settings.inventorySettings.size,
    settings.inventorySettings.inventoryType,
    { p, inv ->
        settings.inventorySettings.onOpen.executeActions(player) { p, str -> str }
    },
    { p, inv ->
        settings.inventorySettings.onClose.executeActions(player) { p, str -> str }
        if (!future.isDone) {
            when (settings.onClose) {
                InventoryRerollInput.Action.REROLL -> {
                    future.complete(RerollManager.RerollResult(true))
                }

                InventoryRerollInput.Action.CLAIM -> {
                    future.complete(RerollManager.RerollResult(false))
                }

                InventoryRerollInput.Action.CANCEL -> {
                    inv.open(player)
                }
            }
        }
    },
    { e, inv ->
        e.isCancelled = true
    }
) {

    fun open() {
        loadItems()
        open(player)
    }

    private fun loadItems() {
        loadButtons()
        loadRewardButtons()
    }

    private fun loadRewardButtons() {
        for ((index, slot) in settings.rewardSlots.slots.withIndex()) {
            val reward = rewards.elementAtOrNull(index) ?: break
            val rewardItem = reward.item.getItem()
            val button = ButtonComponent(
                "reward-${reward.id}",
                10,
                SlotSelection.of(slot),
                HashMap(),
                null,
                { e ->
                    e.isCancelled = true
                },
                10,
                { p, str -> str.updatePAPIPlaceholders(player) },
                rewardItem
            )
            addComponent(button)
        }
    }

    private fun loadButtons() {
        for (button in settings.inventorySettings.buttons) {
            addComponent(
                button.create(
                    { p, str -> str },
                    { e ->
                        val id = button.id.lowercase()
                        if (id == "reroll") {
                            player.closeInventory()
                            future.complete(RerollManager.RerollResult(true))
                            player.closeInventory()
                        } else if (id == "claim") {
                            future.complete(RerollManager.RerollResult(false))
                            player.closeInventory()
                        }
                        e.isCancelled = true
                    }
                )
            )
        }
        if (settings.clearBottomInventory) {
            val airButton = ButtonComponent(
                "aquaticcrates:clear-button",
                -10,
                SlotSelection((size..size + 35).toMutableSet()),
                HashMap(),
                null,
                { e -> e.isCancelled = true },
                1000,
                { p, str -> str },
                ItemStack(Material.AIR)
            )
            addComponent(airButton)
        }
    }
}