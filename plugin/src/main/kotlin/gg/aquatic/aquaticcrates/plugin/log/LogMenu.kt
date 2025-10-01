package gg.aquatic.aquaticcrates.plugin.log

import gg.aquatic.aquaticcrates.api.player.CrateProfileDriver
import gg.aquatic.aquaticcrates.api.player.HistoryHandler
import gg.aquatic.waves.input.impl.ChatInput
import gg.aquatic.waves.inventory.ButtonType
import gg.aquatic.waves.menu.MenuComponent
import gg.aquatic.waves.menu.PrivateAquaticMenu
import gg.aquatic.waves.menu.component.Button
import gg.aquatic.waves.util.*
import gg.aquatic.waves.util.task.AsyncScope
import gg.aquatic.waves.util.task.BukkitScope
import kotlinx.coroutines.launch
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.concurrent.TimeUnit

class LogMenu(val settings: LogMenuSettings, player: Player) : PrivateAquaticMenu(
    settings.menuSettings.title,
    settings.menuSettings.type, player,true
) {

    var crateFilter: String? = null
        private set

    var playerFilter: String? = null
        private set

    var page = 0
        private set

    var sorting: CrateProfileDriver.Sorting = CrateProfileDriver.Sorting.NEWEST
        private set

    @Volatile
    var isLoading = false
        private set

    var hasNextPage = false
        private set

    private val entryComponents = ArrayList<MenuComponent>()

    init {
        loadButtons()
        loadEntries()
    }

    private fun loadButtons() {
        for ((id, component) in settings.menuSettings.components) {
            addComponent(component.create(
                { str, menu ->
                    str.updatePAPIPlaceholders(player)
                        .replace("%page%", "${page + 1}")
                        .replace("%sorting%", sorting.name.lowercase().replaceFirstChar { it.uppercase() })
                        .replace("%player-filter%", playerFilter ?: "")
                        .replace("%crate-filter%", crateFilter ?: "")
                },
                { e ->
                    if (id == "next-page") {
                        if (isLoading) return@create
                        if (!hasNextPage) return@create
                        page++
                        loadEntries()
                    }
                    else if (id == "prev-page") {
                        if (isLoading) return@create
                        if (page <= 0) return@create
                        page--
                        loadEntries()
                    } else if (id == "player-filter") {
                        if (e.buttonType == ButtonType.RIGHT) {
                            if (playerFilter == null) {
                                return@create
                            }
                            playerFilter = null
                            applyFilters()
                            return@create
                        } else if (e.buttonType == ButtonType.LEFT) {
                            BukkitScope.launch {
                                player.closeInventory()
                            }
                            player.send(
                                """
                                    &7Player Filter
                                    
                                    &fIn order to apply the player filter, please enter the player name you would like to filter by.
                                    &fIf you want to cancel the edit, please type &ccancel&f.
                                """.trimIndent().toMMComponent()
                            )
                            ChatInput.createHandle().await(player).thenAccept {
                                val input = it ?: ""
                                if (input.isEmpty()) {
                                    playerFilter = null
                                    applyFilters()
                                    open()
                                    return@thenAccept
                                }
                                playerFilter = input
                                applyFilters()
                                open()
                                return@thenAccept
                            }
                        }
                    } else if (id == "crate-filter") {
                        if (e.buttonType == ButtonType.RIGHT) {
                            if (crateFilter == null) {
                                return@create
                            }
                            crateFilter = null
                            applyFilters()
                            return@create
                        }
                        else if (e.buttonType == ButtonType.LEFT) {
                            BukkitScope.launch {
                                player.closeInventory()
                            }
                            player.send(
                                """
                                    &7Crate Filter
                                    
                                    &fIn order to apply the crate filter, please enter the crate ID you would like to filter by.
                                    &fIf you want to cancel the edit, please type &ccancel&f.
                                """.trimIndent().toMMComponent()
                            )
                            ChatInput.createHandle().await(player).thenAccept {
                                val input = it ?: ""
                                if (input.isEmpty()) {
                                    crateFilter = null
                                    applyFilters()
                                    open()
                                    return@thenAccept
                                }
                                crateFilter = input
                                applyFilters()
                                open()
                                return@thenAccept
                            }
                            return@create
                        }
                    } else if (id == "sort") {
                        val newSorting = if (sorting.ordinal + 1 >= CrateProfileDriver.Sorting.entries.size) {
                            CrateProfileDriver.Sorting.entries[0]
                        } else CrateProfileDriver.Sorting.entries[sorting.ordinal + 1]
                        sorting = newSorting
                        applyFilters()
                        return@create
                    }
                }))
        }
    }

    private fun applyFilters() {
        page = 0
        loadEntries()
    }

    private fun loadEntries() {
        isLoading = true

        for (entryComponent in entryComponents.toMutableList()) {
            removeComponent(entryComponent)
        }
        entryComponents.clear()

        AsyncScope.launch {
            val offset = page * settings.logSlots.size
            val limit = settings.logSlots.size + 1

            val entries = HistoryHandler.loadLogEntries(offset, limit, playerFilter, crateFilter, sorting)
            hasNextPage = entries.size >= limit

            for ((index, logSlot) in settings.logSlots.withIndex()) {
                val (playerName, entry) = entries.elementAtOrNull(index) ?: break

                val item = ItemStack(Material.PAPER).apply {
                    val newLore = mutableListOf(
                        " ".toMMComponent(),
                        "<white>Player: <yellow>$playerName".toMMComponent(),
                        "<white>Timestamp: <yellow>${entry.timeStamp}".toMMComponent(),
                        "<white>Rewards: <yellow>${entry.rewardIds.size}".toMMComponent(),
                        )

                    for ((rewardId, amount) in entry.rewardIds) {
                        newLore += "<white> - <yellow>$rewardId <gray>x$amount".toMMComponent()
                    }

                    newLore += " ".toMMComponent()
                    newLore += "<gray>${entry.timeStamp.toFriendlyTimeFromSeconds()}".toMMComponent()

                    val meta = this.itemMeta
                    meta.lore(newLore)
                    meta.displayName("<yellow>Crate: ${entry.crateId}".toMMComponent())
                    this.itemMeta = meta
                }

                val component = Button("entry_$index", item, listOf(logSlot), 1, 20, null)
                entryComponents += component
                addComponent(component)
            }

            isLoading = false
        }
    }
}

fun Long.toFriendlyTimeFromSeconds(): String {
    val currentTime = System.currentTimeMillis()
    val duration = currentTime - this * 60000

    // If the duration is negative (future timestamp), return "Just now"
    if (duration < 0) {
        return "Just now"
    }

    val seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
    if (seconds < 60) {
        return "$seconds seconds ago"
    }
    val minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
    if (minutes < 60) {
        return "$minutes minutes ago"
    }
    val hours = TimeUnit.MILLISECONDS.toHours(duration)
    if (hours < 24) {
        return "$hours hours ago"
    }
    val days = TimeUnit.MILLISECONDS.toDays(duration)
    if (days < 30) {
        return "$days days ago"
    }
    val months = days / 30
    if (months < 12) {
        return "$months months ago"
    }
    val years = days / 365
    return "$years years ago"
}