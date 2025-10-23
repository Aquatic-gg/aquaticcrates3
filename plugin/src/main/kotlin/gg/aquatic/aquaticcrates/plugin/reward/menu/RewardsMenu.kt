package gg.aquatic.aquaticcrates.plugin.reward.menu

import gg.aquatic.aquaticcrates.api.player.crateEntry
import gg.aquatic.waves.menu.MenuComponent
import gg.aquatic.waves.menu.PrivateAquaticMenu
import gg.aquatic.waves.menu.component.Button
import gg.aquatic.waves.profile.toAquaticPlayer
import gg.aquatic.waves.util.task.BukkitCtx
import gg.aquatic.waves.util.toMMComponent
import gg.aquatic.waves.util.toMMString
import gg.aquatic.waves.util.updatePAPIPlaceholders
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import org.bukkit.entity.Player
import java.util.Collections

class RewardsMenu(val settings: RewardsMenuSettings, player: Player) : PrivateAquaticMenu(
    settings.invSettings.title.toMMString().updatePAPIPlaceholders(player).toMMComponent(),
    settings.invSettings.type, player, true
) {
    private var page = 0
    private val rewardComponents = Collections.synchronizedSet<MenuComponent>(HashSet())

    init {
        val crateEntry = player.toAquaticPlayer()?.crateEntry()
        for ((id, component) in settings.invSettings.components) {
            addComponent(component.create({ str, _ ->
                str.updatePAPIPlaceholders(player)
            }, { _ ->
                if (id == "next-page") {
                    if (crateEntry == null) return@create
                    if (crateEntry.rewardContainer.items.size / settings.rewardSlots.size < page + 1) return@create
                    page++
                    rewardComponents.forEach { removeComponent(it) }
                    loadRewards()
                } else if (id == "prev-page") {
                    if (page <= 0) return@create
                    page--
                    rewardComponents.forEach { removeComponent(it) }
                    loadRewards()
                }
            }))
        }
        loadRewards()
    }

    @Volatile
    private var processing = false

    private fun loadRewards() {
        val crateEntry = player.toAquaticPlayer()?.crateEntry() ?: return

        val entries = crateEntry.rewardContainer.items.entries

        entries.removeIf { (_, amount) -> amount <= 0 }

        for ((index, rewardSlot) in settings.rewardSlots.withIndex()) {
            val rewardIndex = page * settings.rewardSlots.size + index
            val (item, amount) = entries.elementAtOrNull(rewardIndex)?.toPair() ?: break
            if (amount <= 0) continue

            val button = Button(
                "reward-${rewardSlot}",
                item.clone().apply {
                    val meta = this.itemMeta
                    meta.lore(
                        (meta.lore() ?: emptyList()) + settings.additionalRewardLore.map { it.toMMComponent() }
                    )
                    this.itemMeta = meta
                },
                listOf(rewardSlot),
                10,
                1,
                null,
                { true },
                textUpdater = { str, _ -> str.updatePAPIPlaceholders(player).replace("%amount%", amount.toString()) },
                onClick = { e ->
                    if (processing) return@Button
                    processing = true
                    var remaining = crateEntry.rewardContainer.items.remove(item) ?: return@Button
                    rewardComponents.forEach { removeComponent(it) }
                    rewardComponents.clear()

                    BukkitCtx.scope.async {
                        val maxStackSize = item.maxStackSize
                        while (remaining > 0) {
                            if (remaining > maxStackSize) {
                                for ((_, i) in player.inventory.addItem(item.asQuantity(maxStackSize))) {
                                    player.location.world!!.dropItem(player.location, i)
                                }
                                remaining -= maxStackSize
                            } else {
                                for ((_, i) in player.inventory.addItem(item.asQuantity(remaining))) {
                                    player.location.world!!.dropItem(player.location, i)
                                }
                                break
                            }
                        }
                    }.asCompletableFuture().exceptionally {
                        it.printStackTrace()
                    }.thenRun {
                        loadRewards()
                        updateComponents()
                        processing = false
                    }
                }
            )
            rewardComponents += button
            addComponent(button)
        }
    }

}