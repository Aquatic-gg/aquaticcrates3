package gg.aquatic.aquaticcrates.api.player

import gg.aquatic.aquaticcrates.api.player.CrateProfileEntry.HistoryType
import gg.aquatic.waves.profile.toAquaticPlayer
import org.bukkit.entity.Player

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

    fun history(historyType: HistoryType, player: Player): Int {
        val crateEntry = player.toAquaticPlayer()!!.crateEntry()
        var total = 0
        crateEntry.openHistory.forEach { (_, rewardHistory) ->
            for ((_, historyMap) in rewardHistory) {
                historyMap[historyType]?.let { total += it }
            }
        }
        return total
    }

    fun history(crateId: String, historyType: HistoryType, player: Player): Int {
        val crateEntry = player.toAquaticPlayer()!!.crateEntry()
        var total = 0
        crateEntry.openHistory[crateId]?.forEach { (_, historyMap) ->
            historyMap[historyType]?.let { total += it }
        }
        return total
    }
    fun history(crateId: String, rewardId: String, historyType: HistoryType, player: Player): Int {
        val crateEntry = player.toAquaticPlayer()!!.crateEntry()
        return crateEntry.openHistory[crateId]?.get(rewardId)?.get(historyType) ?: 0
    }
}