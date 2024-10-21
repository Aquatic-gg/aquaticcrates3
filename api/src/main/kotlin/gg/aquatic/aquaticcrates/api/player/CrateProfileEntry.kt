package gg.aquatic.aquaticcrates.api.player

import gg.aquatic.waves.profile.AquaticPlayer
import gg.aquatic.waves.profile.module.ProfileModuleEntry
import java.sql.Connection

class CrateProfileEntry(aquaticPlayer: AquaticPlayer) : ProfileModuleEntry(aquaticPlayer) {

    val balance = hashMapOf<String, Int>()

    val newEntries = ArrayList<OpenHistoryEntry>()
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

    override fun save(connection: Connection) {

    }

    fun balance(id: String): Int {
        return balance.getOrDefault(id, 0)
    }

    fun give(amount: Int, id: String) {
        balance[id] = balance(id) + amount
    }

    fun take(amount: Int, id: String) {
        val newBalance = balance(id) - amount
        if (newBalance < 0) {
            balance[id] = 0
        } else
            balance[id] = newBalance
    }

    fun set(amount: Int, id: String) {
        balance[id] = amount
    }

    fun has(amount: Int, id: String): Boolean {
        return balance(id) >= amount
    }

    class OpenHistoryEntry(
        val timestamp: Long,
        val crateId: String,
        val rewardId: String,
    ) {

    }

    enum class HistoryType {
        ALLTIME,
        MONTHLY,
        WEEKLY,
        DAILY,
    }
}