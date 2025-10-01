package gg.aquatic.aquaticcrates.api.player

import gg.aquatic.aquaticcrates.api.player.CrateProfileDriver.dbDispatcher
import gg.aquatic.aquaticcrates.api.reward.RewardContainer
import gg.aquatic.waves.profile.AquaticPlayer
import gg.aquatic.waves.profile.module.ProfileModuleEntry
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class CrateProfileEntry(aquaticPlayer: AquaticPlayer, val rewardContainer: RewardContainer) :
    ProfileModuleEntry(aquaticPlayer) {

    val balance = hashMapOf<String, Int>()

    // CrateId, Entry
    @Volatile
    var newEntries = Collections.synchronizedList<OpenHistoryContainer>(ArrayList())
        internal set

    // CrateId, Daily/Weekly/Monthly/Alltime, Amount
    val openHistory = ConcurrentHashMap<String, ConcurrentHashMap<HistoryType, Int>>()

    // CrateId:RewardId, Daily/Weekly/Monthly/Alltime, Amount
    val rewardHistory = ConcurrentHashMap<String, ConcurrentHashMap<HistoryType, Int>>()

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

    fun saveAndPrune(toSave: List<OpenHistoryContainer>) {
        CrateProfileDriver.driver.useConnection {
            GlobalScope.launch(dbDispatcher) {
                CrateProfileDriver.saveHistory(this@useConnection, this@CrateProfileEntry, toSave)
            }
            //newEntries.clear()
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
        try {
            CrateProfileDriver.save(connection, this@CrateProfileEntry)
        } catch (e: Exception) {
            e.printStackTrace()
        }
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

    class OpenHistoryContainer(
        val timestamp: Long,
    ) {
        val entries = Collections.synchronizedList(ArrayList<OpenHistoryEntry>())
    }

    data class OpenHistoryEntry(
        val crateId: String,
        val rewardIds: MutableMap<String, Int>,
    ) {

    }

    enum class HistoryType {
        ALLTIME,
        MONTHLY,
        WEEKLY,
        DAILY,
    }
}