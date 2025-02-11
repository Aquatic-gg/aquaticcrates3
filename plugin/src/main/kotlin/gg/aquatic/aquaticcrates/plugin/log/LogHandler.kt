package gg.aquatic.aquaticcrates.plugin.log

import gg.aquatic.aquaticcrates.api.player.CrateProfileDriver
import gg.aquatic.aquaticcrates.api.player.CrateProfileDriver.Sorting
import gg.aquatic.aquaticcrates.api.player.CrateProfileEntry
import gg.aquatic.aquaticcrates.api.player.crateEntry
import gg.aquatic.waves.profile.toAquaticPlayer
import org.bukkit.Bukkit

object LogHandler {

    fun loadLogEntries(
        offset: Int,
        limit: Int,
        playerName: String?,
        crateId: String?,
        sorting: Sorting?,
    ): List<Pair<String, CrateProfileEntry.OpenHistoryEntry>> {

        val cachedLogs = mutableListOf<Pair<String, CrateProfileEntry.OpenHistoryEntry>>()

        // 1. Gather cached logs with pre-sorted logic.
        fun collectPlayerLogs(
            playerName: String,
            crateEntry: CrateProfileEntry
        ): List<Pair<String, CrateProfileEntry.OpenHistoryEntry>> {

            val newEntries = crateEntry.newEntries
            val filteredEntries = if (crateId != null) {
                // Filter entries by crateId if needed
                newEntries[crateId]?.toList() ?: emptyList()
            } else {
                // Combine all crates' entries
                newEntries.flatMap { it.value }
            }

            // If sorting is OLDEST, reverse the filtered entries. Otherwise, use as-is.
            val sortedEntries = when (sorting) {
                Sorting.NEWEST -> filteredEntries // Already sorted by latest
                Sorting.OLDEST -> filteredEntries.asReversed() // Reverse for oldest
                null -> filteredEntries // No specific sorting
            }

            // Apply offset and limit directly to this player's logs
            return sortedEntries.take(limit+offset)
                .map { playerName to it } // Add player name in each entry
        }

        if (playerName != null) {
            // If playerName is specified, get the player's cache directly.
            val player = Bukkit.getPlayer(playerName)
            if (player != null) {
                val playerLogs =
                    collectPlayerLogs(playerName, player.toAquaticPlayer()?.crateEntry() ?: return emptyList())
                cachedLogs.addAll(playerLogs)
            }
        } else {
            // Otherwise, iterate through all online players and collect logs.
            for (onlinePlayer in Bukkit.getOnlinePlayers()) {
                val playerLogs =
                    collectPlayerLogs(onlinePlayer.name, onlinePlayer.toAquaticPlayer()?.crateEntry() ?: continue)
                cachedLogs.addAll(playerLogs)
            }
        }

        val cachedSize = cachedLogs.size

        // 3. Dynamically adjust offset and limit for remaining database logs.
        val remainingOffset =
            maxOf(offset - cachedSize, 0) // Calculate the remaining offset after consuming cached logs.
        /*
        val remainingLimit =
            remainingOffset + limit // Calculate the missing logs required from the database.
         */

        // Query logs from the database if necessary.
        val dbLogs: MutableMap<Int, Pair<String, CrateProfileEntry.OpenHistoryEntry>> =
            CrateProfileDriver.loadLogEntries(
                offset = remainingOffset,
                limit = limit,
                playerName = playerName,
                crateId = crateId,
                sorting = sorting
            )

        // Combine cached and database logs.
        val combinedLogs = cachedLogs + dbLogs.values

        // Sort the combined list.
        val sortedCombinedLogs = (when (sorting) {
            Sorting.NEWEST -> combinedLogs.sortedByDescending { it.second.timestamp }
            Sorting.OLDEST -> combinedLogs.sortedBy { it.second.timestamp }
            null -> combinedLogs // No sorting specified.
        }).toMutableList()

        if (cachedSize > 0 && sorting != Sorting.OLDEST) {
            for ((index, pair) in sortedCombinedLogs.toMutableList().withIndex()) {
                if (index >= offset) {
                    break
                }
                if (pair in cachedLogs) {
                    sortedCombinedLogs.remove(pair)
                }
            }
        }

        // Apply pagination: only return the entries within the requested range (`offset` to `offset + limit`).
        return sortedCombinedLogs.take(limit)
    }
}