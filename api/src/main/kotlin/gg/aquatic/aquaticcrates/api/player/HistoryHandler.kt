package gg.aquatic.aquaticcrates.api.player

import gg.aquatic.aquaticcrates.api.player.CrateProfileEntry.HistoryType
import gg.aquatic.aquaticcrates.api.player.CrateProfileEntry.OpenHistoryEntry
import gg.aquatic.waves.profile.toAquaticPlayer
import gg.aquatic.waves.util.runAsync
import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap

object HistoryHandler {

    // namespace: CrateId, RewardId, Daily/Weekly/Monthly/Alltime, Amount
    //val openHistory = ConcurrentHashMap<String, ConcurrentHashMap<String, ConcurrentHashMap<HistoryType, Int>>>()

    // CrateId, Daily/Weekly/Monthly/Alltime, Amount
    val openHistory = ConcurrentHashMap<String, ConcurrentHashMap<HistoryType, Int>>()
    // CrateId:RewardId, Daily/Weekly/Monthly/Alltime, Amount
    val rewardHistory = ConcurrentHashMap<String,ConcurrentHashMap<HistoryType, Int>>()

    fun registerCrateOpen(player: Player, crateId: String, rewards: Map<String, Int>) {
        val crateEntry = player.toAquaticPlayer()?.crateEntry() ?: return
        val newEntries = crateEntry.newEntries

        val globalCrateHistory = openHistory.getOrPut(crateId) { ConcurrentHashMap() }
        for (historyType in HistoryType.entries) {
            val currentAmount = globalCrateHistory.getOrPut(historyType) { 0 }
            globalCrateHistory[historyType] = currentAmount + 1

            for ((reward, amount) in rewards) {
                val rewardHistory = rewardHistory.getOrPut("$crateId:$reward") { ConcurrentHashMap() }
                val currentRewardAmount = rewardHistory.getOrPut(historyType) { 0 }
                rewardHistory[historyType] = currentRewardAmount + amount
            }
        }

        val entries = newEntries.getOrPut(crateId) { ConcurrentHashMap.newKeySet() }
        entries += OpenHistoryEntry(
            System.currentTimeMillis() / 60000,
            crateId,
            HashMap(rewards)
        )
        val crateHistory = crateEntry.openHistory.getOrPut(crateId) { ConcurrentHashMap() }
        for (historyType in HistoryType.entries) {
            val dailyCrate = crateHistory.getOrPut(historyType) { 0 }
            crateHistory[historyType] = dailyCrate + rewards.values.sum()
            for ((reward, amount) in rewards) {
                val rewardHistory = crateEntry.rewardHistory.getOrPut("$crateId:$reward") { ConcurrentHashMap() }
                val dailyReward = rewardHistory.getOrPut(historyType) { 0 }
                rewardHistory[historyType] = dailyReward + amount
            }
        }
        var totalSize = 0
        for ((_, histories) in newEntries) {
            totalSize += histories.size
            if (totalSize >= 500) {
                runAsync {
                    crateEntry.saveAndPrune()
                }
                break
            }
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
}