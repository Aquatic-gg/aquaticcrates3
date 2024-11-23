package gg.aquatic.aquaticcrates.plugin.pouch

import gg.aquatic.aquaticseries.lib.betterinventory2.AquaticInventory
import gg.aquatic.aquaticseries.lib.betterinventory2.SlotSelection
import gg.aquatic.aquaticseries.lib.betterinventory2.component.ButtonComponent
import gg.aquatic.aquaticseries.lib.util.executeActions
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.HashMap

class PouchPreviewMenu(
    val player: Player,
    val pouch: RewardPouch,
) : AquaticInventory(
    pouch.previewMenuSettings.invSettings!!.title,
    pouch.previewMenuSettings.invSettings.size,
    pouch.previewMenuSettings.invSettings.inventoryType,
    { p, inv ->
        pouch.previewMenuSettings.invSettings.onOpen.executeActions(player) { p, str -> str }
    },
    { p, inv ->
        pouch.previewMenuSettings.invSettings.onClose.executeActions(player) { p, str -> str }
    },
    { e, inv ->

    }
) {

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
        for (button in pouch.previewMenuSettings.invSettings!!.buttons) {
            addComponent(
                button.create(
                    { p, str -> str },
                    { e ->
                        e.isCancelled = true
                    }
                )
            )
        }
        if (pouch.previewMenuSettings.clearBottomInventory) {
            val airButton = ButtonComponent(
                "aquaticcrates:clear-button",
                -10,
                SlotSelection((size..size+35).toMutableSet()),
                HashMap(),
                null,
                { e-> e.isCancelled = true },
                1000,
                { p, str -> str },
                ItemStack(Material.AIR)
            )
            addComponent(airButton)
        }

        /*
        val rewards = pouch.getPossibleRewards(player).values
        for ((index, rewardSlot) in pouch.previewMenuSettings.rewardSlots.withIndex()) {
            val rewardIndex = page * pouch.previewMenuSettings.rewardSlots.size + index
            if (rewardIndex >= pouch.rewards.size) break
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
                },
                10,
                { p, str -> str },
                rewardItem
            )
            addComponent(button)
        }
         */
    }
}