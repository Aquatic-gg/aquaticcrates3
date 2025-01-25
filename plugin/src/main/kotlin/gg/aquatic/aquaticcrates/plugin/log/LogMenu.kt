package gg.aquatic.aquaticcrates.plugin.log

import gg.aquatic.aquaticcrates.api.player.CrateProfileDriver
import gg.aquatic.waves.menu.MenuComponent
import gg.aquatic.waves.menu.PrivateAquaticMenu
import gg.aquatic.waves.menu.component.Button
import gg.aquatic.waves.util.item.modifyFastMeta
import gg.aquatic.waves.util.runAsync
import gg.aquatic.waves.util.toMMComponent
import gg.aquatic.waves.util.updatePAPIPlaceholders
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.concurrent.TimeUnit

class LogMenu(val settings: LogMenuSettings, player: Player) : PrivateAquaticMenu(
    settings.menuSettings.title,
    settings.menuSettings.type, player,
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
            component.create(
                { str, menu ->
                    str.updatePAPIPlaceholders(player)
                },
                { e ->
                    if (id == "next-page") {
                        if (isLoading) return@create
                        if (!hasNextPage) return@create
                        page++
                        loadEntries()
                    }
                })
        }
    }

    private fun applyFilters() {
        page = 0
        loadEntries()
    }

    private fun loadEntries() {
        isLoading = true

        for (entryComponent in entryComponents) {
            removeComponent(entryComponent)
        }
        entryComponents.clear()

        runAsync {
            val offset = page * settings.logSlots.size
            val limit = settings.logSlots.size + 1

            val entriesMap = CrateProfileDriver.loadLogEntries(offset, limit, playerFilter, crateFilter, sorting)
            hasNextPage = entriesMap.size >= limit

            val entries = entriesMap.entries
            for ((index, logSlot) in settings.logSlots.withIndex()) {
                val (entryId, pair) = entries.elementAtOrNull(index) ?: break
                val (playerName, entry) = pair

                val item = ItemStack(Material.PAPER).apply {
                    val newLore = mutableListOf(
                        " ".toMMComponent(),
                        "<white>Player: <yellow>$playerName".toMMComponent(),
                        "<white>Timestamp: <yellow>${entry.timestamp}".toMMComponent(),
                        "<white>Rewards: <yellow>${entry.rewardIds.size}".toMMComponent(),
                        )

                    for ((rewardId, amount) in entry.rewardIds) {
                        newLore += "<white> - <yellow>$rewardId <gray>x$amount".toMMComponent()
                    }

                    newLore += " ".toMMComponent()
                    newLore += "<gray>${toFriendlyTime(entry.timestamp)}".toMMComponent()

                    modifyFastMeta {
                        this.displayName = "<yellow>Crate: ${entry.crateId} <gray>(#$entryId)".toMMComponent()
                        this.lore = newLore
                    }
                }

                val component = Button("entry_$entryId", item, listOf(logSlot), 1, 20, null)
                entryComponents += component
                addComponent(component)
            }

            isLoading = false
        }
    }

    private fun toFriendlyTime(timestamp: Long): String {
        val currentTime = System.currentTimeMillis()
        val duration = currentTime - timestamp * 60000

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

}