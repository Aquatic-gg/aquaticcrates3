package gg.aquatic.aquaticcrates.plugin.preview

import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate
import gg.aquatic.aquaticcrates.plugin.crate.BasicCrate
import gg.aquatic.aquaticcrates.plugin.reward.RolledRewardImpl
import gg.aquatic.waves.menu.PrivateAquaticMenu
import gg.aquatic.waves.menu.SlotSelection
import gg.aquatic.waves.menu.component.Button
import gg.aquatic.waves.util.*
import gg.aquatic.waves.util.chance.randomItem
import gg.aquatic.waves.util.item.modifyFastMeta
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class CratePreviewMenu(
    player: Player,
    val crate: BasicCrate,
    val placedCrate: SpawnedCrate?,
    val settings: CratePreviewMenuSettings,
    val page: Int
) : PrivateAquaticMenu(
    settings.invSettings.title.toMMString().updatePAPIPlaceholders(player).toMMComponent(),
    settings.invSettings.type,
    player
) {
    val rewards = crate.rewardManager.getPossibleRewards(player).values

    init {
        loadItems()
        for (component in components.values) {
            updateComponent(component)
        }
    }

    private fun openPage(page: Int) {
        val settings = crate.previewMenuSettings[page]
        val menu = CratePreviewMenu(player, crate, placedCrate, settings, page)
        menu.open()
    }

    private fun loadItems() {
        loadButtons()
        loadRewards()
        loadRandomRewards()
    }

    private fun loadButtons() {
        for ((id, component) in settings.invSettings.components) {
            val comp = component.create(
                { str, menu ->
                    str
                        .replace("%page%", "${page + 1}")
                        .replace("%player%", player.name)
                        .updatePAPIPlaceholders(player)
                },
                { e ->
                    if (id == "next-page") {
                        if (!hasNextPage()) return@create
                        openPage(page + 1)
                    } else if (id == "prev-page") {
                        if (page <= 0) return@create
                        openPage(page - 1)
                    } else if (id == "open") {
                        runSync {
                            player.closeInventory()
                            runAsync {
                                crate.tryOpen(player, placedCrate?.location ?: player.location.clone(), placedCrate)
                            }
                        }
                    }
                }
            )
            components += id to comp
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

    private fun loadRewards() {
        var lowerIndex = 0
        for ((index, page) in crate.previewMenuSettings.withIndex()) {
            if (index == this.page) break
            lowerIndex += page.rewardSlots.size
        }

        for ((index, rewardSlot) in settings.rewardSlots.withIndex()) {
            //val rewardIndex = page * settings.rewardSlots.size + index
            val rewardIndex = lowerIndex + index
            if (rewardIndex >= rewards.size) break
            val reward = rewards.elementAtOrNull(rewardIndex) ?: break
            val rewardItem = reward.item.getItem().clone()

            rewardItem.modifyFastMeta {
                lore = mutableListOf<Component>().apply {
                    addAll(lore)
                    addAll(settings.additionalRewardLore.map {
                        it.toMMComponent().decoration(TextDecoration.ITALIC, TextDecoration.State.NOT_SET)
                    })
                }
            }

            val button = Button(
                "reward-${reward.id}",
                rewardItem,
                SlotSelection.of(rewardSlot).slots,
                10,
                settings.updateRewardItemsEvery,
                null, textUpdater = { str, menu ->
                    str.updatePAPIPlaceholders(player)
                        .replace("%chance%", (reward.chance * 100.0).decimals(2))
                        .replace("%rarity%", reward.rarity.displayName)
                }, onClick = { _ ->
                    if (player.hasPermission("aquaticcrates.admin")) {
                        RolledRewardImpl(reward, reward.amountRanges.randomItem()?.randomNum ?: 1).give(player, false)
                    }
                }
            )
            components += button.id to button
        }
    }

    private fun loadRandomRewards() {
        for (slot in settings.randomRewards.slots) {
            val button = RandomRewardComponent(
                crate,
                rewards,
                settings.randomRewards.changeDuration,
                { e ->

                },
                10,
                listOf(slot),
                { str, _ -> str.updatePAPIPlaceholders(player) },
                settings
            )
            components += button.id to button
        }
    }

    private fun hasNextPage(): Boolean {
        return (crate.previewMenuSettings.size > page + 1)
    }

}