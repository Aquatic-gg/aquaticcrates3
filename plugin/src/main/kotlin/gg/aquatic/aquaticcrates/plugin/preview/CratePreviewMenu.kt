package gg.aquatic.aquaticcrates.plugin.preview

import gg.aquatic.aquaticcrates.plugin.crate.BasicCrate
import gg.aquatic.aquaticseries.lib.betterinventory2.AquaticInventory
import gg.aquatic.aquaticseries.lib.betterinventory2.SlotSelection
import gg.aquatic.aquaticseries.lib.betterinventory2.component.ButtonComponent
import gg.aquatic.aquaticseries.lib.util.executeActions
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.HashMap
import kotlin.math.ceil

class CratePreviewMenu(
    val player: Player,
    val crate: BasicCrate,
) : AquaticInventory(
    crate.previewMenuSettings.invSettings!!.title,
    crate.previewMenuSettings.invSettings.size,
    crate.previewMenuSettings.invSettings.inventoryType,
    { p, inv ->
        crate.previewMenuSettings.invSettings.onOpen.executeActions(player) { p, str -> str }
    },
    { p, inv ->
        crate.previewMenuSettings.invSettings.onClose.executeActions(player) { p, str -> str }
    },
    { e, inv ->

    }
) {

    val rewards = crate.rewardManager.getPossibleRewards(player).values

    fun open() {
        loadItems(0)
        open(player)
    }

    private fun openPage(page: Int) {
        clearComponents()
        loadItems(page)
        updateComponents(player)
    }

    private fun loadItems(page: Int) {
        for (button in crate.previewMenuSettings.invSettings!!.buttons) {
            addComponent(
                button.create(
                    { p, str -> str },
                    { e ->
                        val id = button.id.lowercase()
                        if (id == "next-page") {
                            if (!hasNextPage(page)) return@create
                            openPage(page + 1)
                        } else if (id == "prev-page") {
                            if (page <= 0) return@create
                            openPage(page - 1)
                        }
                        e.isCancelled = true
                    }
                )
            )
        }
        if (crate.previewMenuSettings.clearBottomInventory) {
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

        for ((index, rewardSlot) in crate.previewMenuSettings.rewardSlots.withIndex()) {
            val rewardIndex = page * crate.previewMenuSettings.rewardSlots.size + index
            if (rewardIndex >= crate.rewardManager.rewards.size) break
            val reward = rewards.elementAtOrNull(rewardIndex) ?: break
            val rewardItem = reward.item.getItem()

            val button = ButtonComponent(
                "reward-${reward.id}",
                10,
                SlotSelection.of(rewardSlot),
                HashMap(),
                null,
                { e ->
                    e.isCancelled = true
                    val p = e.whoClicked as? Player ?: return@ButtonComponent
                    if (p.hasPermission("aquaticcrates.admin")) {
                        reward.give(player, 1)
                    }
                },
                10,
                { p, str -> str },
                rewardItem
            )
            addComponent(button)
        }

    }

    private fun hasNextPage(currentPage: Int): Boolean {
        val amt = ceil(rewards.size / crate.previewMenuSettings.rewardSlots.size.toDouble())
        return currentPage < amt
    }

}