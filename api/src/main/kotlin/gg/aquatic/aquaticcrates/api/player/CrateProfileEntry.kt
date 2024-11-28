package gg.aquatic.aquaticcrates.api.player

import gg.aquatic.waves.profile.AquaticPlayer
import gg.aquatic.waves.profile.module.ProfileModuleEntry
import java.sql.Connection

class CrateProfileEntry(aquaticPlayer: AquaticPlayer) : ProfileModuleEntry(aquaticPlayer) {

    val balance = hashMapOf<String, Int>()

    // CrateId, Entry
    val newEntries = hashMapOf<String,MutableList<OpenHistoryEntry>>()
    // CrateId, Daily/Weekly/Monthly/Alltime, Amount
    val openHistory = hashMapOf<String, HashMap<HistoryType, Int>>()
    // CrateId:RewardId, Daily/Weekly/Monthly/Alltime, Amount
    val rewardHistory = hashMapOf<String,HashMap<HistoryType, Int>>()

    fun openHistory(historyType: HistoryType): Int {
        var total = 0

        for ((_, history) in openHistory) {
            history[historyType]?.let { total += it }
        }
        return total
    }
    fun rewardHistory(historyType: HistoryType): Int {
        var total = 0
        for ((_, history) in rewardHistory) {
            history[historyType]?.let { total += it }
        }
        return total
    }

    fun registerCrateOpen(crateId: String, rewards: Map<String,Int>) {
        val entries = newEntries.getOrPut(crateId) { arrayListOf() }
        entries += OpenHistoryEntry(
            System.currentTimeMillis()/60000,
            crateId,
            HashMap(rewards)
        )
        val crateHistory = openHistory.getOrPut(crateId) { hashMapOf() }
        for (historyType in HistoryType.entries) {
            val dailyCrate = crateHistory.getOrPut(historyType) { 0 }
            crateHistory[historyType] = dailyCrate + rewards.values.sum()
            for ((reward, amount) in rewards) {
                val rewardHistory = this.rewardHistory.getOrPut("$crateId:$reward") { hashMapOf() }
                val dailyReward = rewardHistory.getOrPut(historyType) { 0 }
                rewardHistory[historyType] = dailyReward + amount
            }
        }
    }

    fun openHistory(crateId: String, historyType: HistoryType): Int {
        var total = 0
        openHistory[crateId]?.get(historyType)?.let { total += it }
        return total
    }
    fun rewardHistory(crateId: String, rewardId: String, historyType: HistoryType): Int {
        var total = 0
        val rewardHistory = rewardHistory["$crateId:$rewardId"] ?: return total
        rewardHistory[historyType]?.let { total += it }
        return total
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
        val rewardIds: HashMap<String,Int>,
    ) {

    }

    enum class HistoryType {
        ALLTIME,
        MONTHLY,
        WEEKLY,
        DAILY,
    }
}