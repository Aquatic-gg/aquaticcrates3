package gg.aquatic.aquaticcrates.api.player

import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.player.CrateProfileDriver.Sorting
import gg.aquatic.aquaticcrates.api.player.CrateProfileEntry.HistoryType
import gg.aquatic.aquaticcrates.api.player.CrateProfileEntry.OpenHistoryEntry
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.waves.profile.toAquaticPlayer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object HistoryHandler {

    // namespace: CrateId, RewardId, Daily/Weekly/Monthly/Alltime, Amount
    //val openHistory = ConcurrentHashMap<String, ConcurrentHashMap<String, ConcurrentHashMap<HistoryType, Int>>>()

    val latestRewards = ConcurrentHashMap<String, MutableList<LatestReward>>()

    // CrateId, Daily/Weekly/Monthly/Alltime, Amount
    val openHistory = ConcurrentHashMap<String, ConcurrentHashMap<HistoryType, Int>>()

    // CrateId:RewardId, Daily/Weekly/Monthly/Alltime, Amount
    val rewardHistory = ConcurrentHashMap<String, ConcurrentHashMap<HistoryType, Int>>()

    fun registerCrateOpen(crateProfile: CrateProfileEntry, crateId: String, rewards: Map<Reward, Int>) {
        val newEntries = crateProfile.newEntries
        try {
            val globalCrateHistory = openHistory.getOrPut(crateId) { ConcurrentHashMap() }
            for (historyType in HistoryType.entries) {
                val currentAmount = globalCrateHistory.getOrPut(historyType) { 0 }
                globalCrateHistory[historyType] = currentAmount + 1

                for ((reward, amount) in rewards) {
                    CrateHandler.crates[crateId]?.let { crate ->
                        if (crate is OpenableCrate) {
                            val latestReward = LatestReward(reward, System.currentTimeMillis() / 60000, amount, crateProfile.aquaticPlayer.username)
                            val list = latestRewards.getOrPut(crateId) { Collections.synchronizedList(ArrayList()) }
                            list.add(latestReward)
                            if (list.size > 10) {
                                for (i in 10 until Int.MAX_VALUE) {
                                    if (list.size <= i) break
                                    try {
                                        list.removeAt(i)
                                    } catch (_: Exception) {
                                        break
                                    }
                                }
                            }
                        }
                    }

                    val rewardHistory = rewardHistory.getOrPut("$crateId:$reward") { ConcurrentHashMap() }
                    val currentRewardAmount = rewardHistory.getOrPut(historyType) { 0 }
                    rewardHistory[historyType] = currentRewardAmount + amount
                }
            }

            val entries = newEntries
            val timeStamp = System.currentTimeMillis() / 60000

            val lastContainer = entries.lastOrNull()
            val container = if (lastContainer != null && lastContainer.timestamp == timeStamp) {
                lastContainer
            } else {
                val container = CrateProfileEntry.OpenHistoryContainer(
                    timeStamp
                )
                entries += container
                container
            }
            container.entries += OpenHistoryEntry(
                crateId,
                rewards.mapKeys { it.key.id }.toMutableMap()
            )
            val crateHistory = crateProfile.openHistory.getOrPut(crateId) { ConcurrentHashMap() }
            for (historyType in HistoryType.entries) {
                val dailyCrate = crateHistory.getOrPut(historyType) { 0 }
                crateHistory[historyType] = dailyCrate + rewards.values.sum()
                for ((reward, amount) in rewards) {
                    val rewardHistory = crateProfile.rewardHistory.getOrPut("$crateId:$reward") { ConcurrentHashMap() }
                    val dailyReward = rewardHistory.getOrPut(historyType) { 0 }
                    rewardHistory[historyType] = dailyReward + amount
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val size = newEntries.sumOf { it.entries.size }
        if (size >= 500) {
            val entriesCopy = ArrayList(newEntries)
            newEntries.clear()
            crateProfile.saveAndPrune(entriesCopy)
        }
    }

    fun history(historyType: HistoryType): Int {
        var total = 0

        openHistory.forEach { (_, history) ->
            history[historyType]?.let { total += it }
        }
        return total
    }


    fun history(crateId: String, historyType: HistoryType): Int {
        var total = 0

        openHistory[crateId]?.let { history ->
            history[historyType]?.let { total += it }
        }
        return total
    }

    fun rewardHistory(crateId: String, rewardId: String, historyType: HistoryType): Int {
        return rewardHistory["$crateId:$rewardId"]?.get(historyType) ?: 0
    }

    fun history(historyType: HistoryType, player: Player): Int {
        var total = 0

        total += player.toAquaticPlayer()?.crateEntry()?.openHistory(historyType) ?: 0
        return total
    }

    fun history(crateId: String, historyType: HistoryType, player: Player): Int {
        var total = 0
        total += player.toAquaticPlayer()?.crateEntry()?.openHistory(crateId, historyType) ?: 0
        return total
    }

    fun rewardHistory(crateId: String, rewardId: String, historyType: HistoryType, player: Player): Int {
        val crateEntry = player.toAquaticPlayer()?.crateEntry()
        return crateEntry?.rewardHistory(crateId, rewardId, historyType) ?: 0
    }

    fun loadLogEntries(
        offset: Int,
        limit: Int,
        playerName: String?,
        crateId: String?,
        sorting: Sorting?,
    ): List<Pair<String, CrateProfileDriver.LogEntry>> {

        val cachedLogs = mutableListOf<Pair<String, CrateProfileDriver.LogEntry>>()

        // 1. Gather cached logs with pre-sorted logic.
        fun collectPlayerLogs(
            playerName: String,
            crateEntry: CrateProfileEntry
        ): List<Pair<String, CrateProfileDriver.LogEntry>> {

            val newEntries = crateEntry.newEntries
            val filteredEntries = ArrayList<CrateProfileDriver.LogEntry>()
            for (container in newEntries) {
                for (entry in container.entries) {
                    if (crateId != null && entry.crateId != crateId) continue
                    filteredEntries += CrateProfileDriver.LogEntry(
                        container.timestamp,
                        entry.crateId,
                        entry.rewardIds
                    )
                }
            }

            // If sorting is OLDEST, reverse the filtered entries. Otherwise, use as-is.
            val sortedEntries = when (sorting) {
                Sorting.NEWEST -> filteredEntries // Already sorted by latest
                Sorting.OLDEST -> filteredEntries.asReversed() // Reverse for oldest
                null -> filteredEntries // No specific sorting
            }

            // Apply offset and limit directly to this player's logs
            return sortedEntries.take(limit + offset)
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
        val dbLogs: MutableMap<Int, Pair<String, CrateProfileDriver.LogEntry>> =
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
            Sorting.NEWEST -> combinedLogs.sortedByDescending { it.second.timeStamp }
            Sorting.OLDEST -> combinedLogs.sortedBy { it.second.timeStamp }
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

    class LatestReward(
        val reward: Reward,
        val timestamp: Long,
        val amount: Int,
        val winner: String
    )
}