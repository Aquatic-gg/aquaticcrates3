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

class CratePreviewMenu(
    val player: Player,
    val crate: BasicCrate,
    val settings: CratePreviewMenuSettings,
    val page: Int
) : AquaticInventory(
    settings.invSettings.title,
    settings.invSettings.size,
    settings.invSettings.inventoryType,
    { p, inv ->
        settings.invSettings.onOpen.executeActions(player) { p, str -> str }
    },
    { p, inv ->
        settings.invSettings.onClose.executeActions(player) { p, str -> str }
    },
    { e, inv ->

    }
) {

    val rewards = crate.rewardManager.getPossibleRewards(player).values

    fun open() {
        loadItems()
        open(player)
    }

    private fun openPage(page: Int) {
        val settings = crate.previewMenuSettings[page]
        val menu = CratePreviewMenu(player, crate, settings, page)
        menu.open()
    }

    private fun loadItems() {
        loadButtons()
        loadRewards()
        loadRandomRewards()
    }

    private fun loadButtons() {
        for (button in settings.invSettings.buttons) {
            addComponent(
                button.create(
                    { p, str -> str },
                    { e ->
                        val id = button.id.lowercase()
                        if (id == "next-page") {
                            if (!hasNextPage()) return@create
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

    private fun loadRewards() {
        var lowerIndex = 0
        for ((index,page) in crate.previewMenuSettings.withIndex()) {
            if (index == this.page) break
            lowerIndex += page.rewardSlots.size
        }

        for ((index, rewardSlot) in settings.rewardSlots.withIndex()) {
            //val rewardIndex = page * settings.rewardSlots.size + index
            val rewardIndex = lowerIndex + index
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

    private fun loadRandomRewards() {
        for (slot in settings.randomRewards.slots) {
            val button = RandomRewardComponent(
                crate,
                rewards,
                settings.randomRewards.changeDuration,
                { e ->
                    e.isCancelled = true
                },
                10,
                SlotSelection.of(slot),
                { p, str -> str },
            )
            addComponent(button)
        }
    }

    /*
    private fun refreshRandomRewards() {
        for (randomRewardComponent in randomRewardComponents) {
            removeComponent(randomRewardComponent)
        }
        randomRewardComponents.clear()
        loadRandomRewards()
        updateComponents(player)
    }

    private var ticks = 0
    override fun tick() {
        if (settings.randomRewards.changeDuration <= 0) return
        ticks++
        if (ticks < settings.randomRewards.changeDuration) return
        ticks = 0
        refreshRandomRewards()
    }
     */

    private fun hasNextPage(): Boolean {
        return (crate.previewMenuSettings.size >= page)
    }

}