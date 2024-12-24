package gg.aquatic.aquaticcrates.plugin.reroll.input.inventory

import gg.aquatic.aquaticcrates.api.reroll.RerollManager
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.waves.menu.PrivateAquaticMenu
import gg.aquatic.waves.menu.SlotSelection
import gg.aquatic.waves.menu.component.Button
import gg.aquatic.waves.shadow.com.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCloseWindow
import gg.aquatic.waves.util.runSync
import gg.aquatic.waves.util.updatePAPIPlaceholders
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.concurrent.CompletableFuture

class RerollMenu(
    player: Player,
    val rewards: Collection<Reward>,
    val settings: RerollInventorySettings,
    val future: CompletableFuture<RerollManager.RerollResult>
) : PrivateAquaticMenu(
    settings.inventorySettings.title,
    settings.inventorySettings.type,
    player
) {

    init {
        loadItems()
        for (component in components.values) {
            updateComponent(component)
        }
    }

    private fun loadItems() {
        loadButtons()
        loadRewardButtons()
    }

    private fun loadRewardButtons() {
        for ((index, slot) in settings.rewardSlots.slots.withIndex()) {
            val reward = rewards.elementAtOrNull(index) ?: break
            val rewardItem = reward.item.getItem()
            val button = Button(
                "reward-${reward.id}",
                rewardItem,
                listOf(slot),
                10,
                10,
                null,
                { true },
                { str, _ -> str.updatePAPIPlaceholders(player) },
            )

            components += button.id to button
        }
    }

    private fun loadButtons() {
        for ((id, button) in settings.inventorySettings.components) {
            components += id to button.create(
                { str, menu -> str },
                { e ->
                    if (id == "reroll") {
                        future.complete(RerollManager.RerollResult(true))
                        runSync {
                            player.closeInventory()
                        }
                    } else if (id == "claim") {
                        future.complete(RerollManager.RerollResult(false))
                        runSync {
                            player.closeInventory()
                        }
                    }
                }
            )
        }
        if (settings.clearBottomInventory) {
            val airButton = Button(
                "aquaticcrates:clear-button",
                ItemStack(Material.AIR),
                SlotSelection(((type.size)..((type.size) + 35)).toMutableSet()).slots,
                -10,
                1000,
                null,
                { true },
            )
            components += airButton.id to airButton
        }
    }
}