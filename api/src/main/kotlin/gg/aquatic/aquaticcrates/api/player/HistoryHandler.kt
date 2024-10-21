package gg.aquatic.aquaticcrates.api.player

import gg.aquatic.aquaticcrates.api.player.CrateProfileEntry.HistoryType

object HistoryHandler {

    // CrateId, RewardId, Daily/Weekly/Monthly/Alltime, Amount
    val openHistory = hashMapOf<String, HashMap<String, HashMap<HistoryType, Int>>>()

    fun history(historyType: HistoryType): Int {
        var total = 0
        openHistory.forEach { (_, rewardHistory) ->
            for ((_, historyMap) in rewardHistory) {
                historyMap[historyType]?.let { total += it }
            }
        }
        return total
    }

    fun history(crateId: String, historyType: HistoryType): Int {
        var total = 0
        openHistory[crateId]?.forEach { (_, historyMap) ->
            historyMap[historyType]?.let { total += it }
        }
        return total
    }
    fun history(crateId: String, rewardId: String, historyType: HistoryType): Int {
        return openHistory[crateId]?.get(rewardId)?.get(historyType) ?: 0
    }
}