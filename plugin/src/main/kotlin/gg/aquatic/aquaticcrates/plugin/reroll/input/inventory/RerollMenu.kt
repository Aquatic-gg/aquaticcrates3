package gg.aquatic.aquaticcrates.plugin.reroll.input.inventory

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.reroll.RerollManager
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.waves.menu.PrivateAquaticMenu
import gg.aquatic.waves.menu.SlotSelection
import gg.aquatic.waves.menu.component.Button
import gg.aquatic.waves.util.task.BukkitScope
import gg.aquatic.waves.util.toMMComponent
import gg.aquatic.waves.util.toMMString
import gg.aquatic.waves.util.updatePAPIPlaceholders
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.concurrent.CompletableFuture

class RerollMenu(
    player: Player,
    val rewards: Collection<RolledReward>,
    val settings: RerollInventorySettings,
    val animation: CrateAnimation,
    val future: CompletableFuture<RerollManager.RerollResult>
) : PrivateAquaticMenu(
    animation.updatePlaceholders(settings.inventorySettings.title.toMMString()).updatePAPIPlaceholders(player).toMMComponent(),
    settings.inventorySettings.type,
    player, true
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
            val rewardItem = reward.reward.item.getItem()
            val button = Button(
                "reward-${reward.reward.id}",
                rewardItem,
                listOf(slot),
                10,
                10,
                null,
                { true },
                { str, _ -> animation.updatePlaceholders(str).updatePAPIPlaceholders(player) },
            )

            components += button.id to button
        }
    }

    private fun loadButtons() {
        for ((id, button) in settings.inventorySettings.components) {
            components += id to button.create(
                { str, menu -> animation.updatePlaceholders(str) },
                { e ->
                    if (id == "reroll") {
                        future.complete(RerollManager.RerollResult(true))
                        BukkitScope.launch {
                            player.closeInventory()
                        }
                    } else if (id == "claim") {
                        future.complete(RerollManager.RerollResult(false))
                        BukkitScope.launch {
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
                SlotSelection(((type.size)..((type.size) + 36)).toMutableList()).slots,
                -10,
                1000,
                null,
                { true },
            )
            components += airButton.id to airButton
        }
    }
}