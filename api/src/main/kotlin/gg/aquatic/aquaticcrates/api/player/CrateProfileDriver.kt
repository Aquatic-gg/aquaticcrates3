package gg.aquatic.aquaticcrates.api.player

import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import gg.aquatic.aquaticcrates.api.player.CrateProfileEntry.HistoryType
import gg.aquatic.aquaticcrates.api.reward.RewardContainer
import gg.aquatic.waves.Waves
import gg.aquatic.waves.data.DataDriver
import gg.aquatic.waves.module.WaveModules
import gg.aquatic.waves.profile.AquaticPlayer
import gg.aquatic.waves.profile.ProfilesModule
import org.intellij.lang.annotations.Language
import java.sql.Connection
import java.sql.PreparedStatement
import java.util.concurrent.ConcurrentHashMap

object CrateProfileDriver {
    val driver: DataDriver = (Waves.INSTANCE.modules[WaveModules.PROFILES] as ProfilesModule).driver

    fun get(aquaticPlayer: AquaticPlayer): CrateProfileEntry {
        val entry = CrateProfileEntry(aquaticPlayer, RewardContainer())
        loadKeys(entry)
        loadHistory(entry)
        return entry
    }

    fun loadKeys(entry: CrateProfileEntry) {
        @Language("SQL")
        val sql = "SELECT * FROM aquaticcrates_keys WHERE id = ?"
        driver.executeQuery(sql, {
            setInt(1, entry.aquaticPlayer.index)
        }, {
            while (next()) {
                val key = getString("key_id")
                val value = getInt("amount")

                CrateHandler.crates[key] ?: continue
                entry.balance[key] = value
            }
        })
    }

    fun save(connection: Connection, entry: CrateProfileEntry) {
        saveKeys(connection, entry)
        saveHistory(connection, entry)
    }

    private fun saveKeys(connection: Connection, entry: CrateProfileEntry) {
        connection.prepareStatement("REPLACE INTO aquaticcrates_keys VALUES (?, ?, ?)").use { statement ->
            for ((id, amt) in entry.balance) {
                statement.setInt(1, entry.aquaticPlayer.index)
                statement.setString(2, id)
                statement.setInt(3, amt)
                statement.addBatch()
            }
            statement.executeBatch()
        }
    }

    internal fun saveHistory(connection: Connection, profileEntry: CrateProfileEntry) {
        val insertOpensQuery = "INSERT INTO aquaticcrates_opens (user_id, open_timestamp, crate_id) VALUES (?, ?, ?);"
        val insertRewardsQuery = "INSERT INTO aquaticcrates_rewards (open_id, reward_id, amount) VALUES (?, ?, ?);"

        /*
        connection.prepareStatement(insertOpensQuery, PreparedStatement.RETURN_GENERATED_KEYS).use { opensStmt ->
            connection.prepareStatement(insertRewardsQuery).use { rewardsStmt ->
                for ((userId, entries) in profileEntry.newEntries) {
                    for (entry in entries) {
                        // Insert the 'open' entry
                        opensStmt.setInt(1, userId.toInt()) // Assuming userId is numeric as a string
                        opensStmt.setLong(2, entry.timestamp) // Set timestamp
                        opensStmt.setString(3, entry.crateId) // Set crateId
                        opensStmt.executeUpdate()

                        // Get the generated open_id
                        val generatedKeys = opensStmt.generatedKeys
                        if (generatedKeys.next()) {
                            val openId = generatedKeys.getInt(1) // Retrieve auto-generated open_id

                            // Insert all rewards related to this open entry
                            for ((rewardId, amount) in entry.rewardIds) {
                                rewardsStmt.setInt(1, openId) // Set open_id as FK
                                rewardsStmt.setString(2, rewardId) // Set reward_id
                                rewardsStmt.setInt(3, amount) // Set amount
                                rewardsStmt.addBatch() // Add to batch for performance
                            }
                        }
                    }
                }
                rewardsStmt.executeBatch()
            }
        }
         */

        // Collect auto-generated open IDs and their related rewards
        val openIdToRewards = mutableListOf<Pair<Int, List<Pair<String, Int>>>>()

        // Insert into `aquaticcrates_opens` and retrieve auto-generated keys
        connection.prepareStatement(insertOpensQuery, PreparedStatement.RETURN_GENERATED_KEYS).use { opensStmt ->
            for ((userId, entries) in profileEntry.newEntries) {
                for (entry in entries) {
                    // Prepare batch for "opens" table inserts
                    opensStmt.setInt(1, userId.toInt()) // Assuming userId is numeric in string form
                    opensStmt.setLong(2, entry.timestamp) // Set timestamp
                    opensStmt.setString(3, entry.crateId) // Set crateId
                    opensStmt.addBatch() // Add to batch
                }
            }

            // Execute the batch for "opens" table
            opensStmt.executeBatch()

            // Retrieve auto-generated keys (open_id)
            val generatedKeys = opensStmt.generatedKeys
            for ((_, entries) in profileEntry.newEntries) {
                for (entry in entries) {
                    if (generatedKeys.next()) {
                        val openId = generatedKeys.getInt(1) // Auto-generated `open_id`
                        // Map this `open_id` to the rewards from `rewardIds`
                        val rewards = entry.rewardIds.map { it.key to it.value } // Convert rewardIds into list of (rewardId, amount)
                        openIdToRewards.add(openId to rewards) // Add to mapping
                    }
                }
            }
        }

        // Insert into `aquaticcrates_rewards` using batched rewards
        connection.prepareStatement(insertRewardsQuery).use { rewardsStmt ->
            for ((openId, rewards) in openIdToRewards) {
                for ((rewardId, amount) in rewards) {
                    // Prepare batch for "rewards" table inserts
                    rewardsStmt.setInt(1, openId) // Set foreign key (open_id)
                    rewardsStmt.setString(2, rewardId) // Set reward_id
                    rewardsStmt.setInt(3, amount) // Set reward amount
                    rewardsStmt.addBatch() // Add to batch
                }
            }

            // Execute batch for "rewards" table
            rewardsStmt.executeBatch()
        }
    }

    fun loadHistory(entry: CrateProfileEntry) {
        @Language("SQL")
        val crateHistorySql = "SELECT crate_id, " +
                "       COUNT(*) AS all_time, " +
                "       SUM(CASE WHEN open_timestamp >= strftime('%s', 'now', '-1 day') THEN 1 ELSE 0 END) AS daily, " +
                "       SUM(CASE WHEN open_timestamp >= strftime('%s', 'now', '-7 days') THEN 1 ELSE 0 END) AS weekly, " +
                "       SUM(CASE WHEN open_timestamp >= strftime('%s', 'now', '-1 month') THEN 1 ELSE 0 END) AS monthly " +
                "FROM aquaticcrates_opens " +
                "GROUP BY crate_id;"

        driver.executeQuery(crateHistorySql, { }, {
            while (next()) {
                val crateId = getString("crate_id")
                val allTime = getInt("all_time")
                val daily = getInt("daily")
                val weekly = getInt("weekly")
                val monthly = getInt("monthly")
                entry.openHistory.getOrPut(crateId) { ConcurrentHashMap() }[HistoryType.ALLTIME] = allTime
                entry.openHistory.getOrPut(crateId) { ConcurrentHashMap() }[HistoryType.DAILY] = daily
                entry.openHistory.getOrPut(crateId) { ConcurrentHashMap() }[HistoryType.WEEKLY] = weekly
                entry.openHistory.getOrPut(crateId) { ConcurrentHashMap() }[HistoryType.MONTHLY] = monthly
            }
        })

        @Language("SQL")
        val rewardHistorySql = "SELECT o.crate_id || ':' || r.reward_id AS crate_reward_id, " +
                "       COUNT(*) AS all_time, " +
                "       SUM(CASE WHEN o.open_timestamp >= strftime('%s', 'now', '-1 day') THEN 1 ELSE 0 END) AS daily, " +
                "       SUM(CASE WHEN o.open_timestamp >= strftime('%s', 'now', '-7 days') THEN 1 ELSE 0 END) AS weekly, " +
                "       SUM(CASE WHEN o.open_timestamp >= strftime('%s', 'now', '-1 month') THEN 1 ELSE 0 END) AS monthly " +
                "FROM aquaticcrates_opens o " +
                "         JOIN aquaticcrates_rewards r ON o.id = r.open_id " +
                "GROUP BY crate_reward_id;"
        driver.executeQuery(rewardHistorySql, { }, {
            while (next()) {
                val crateRewardId = getString("crate_reward_id")
                val allTime = getInt("all_time")
                val daily = getInt("daily")
                val weekly = getInt("weekly")
                val monthly = getInt("monthly")
                entry.rewardHistory.getOrPut(crateRewardId) { ConcurrentHashMap() }[HistoryType.ALLTIME] = allTime
                entry.rewardHistory.getOrPut(crateRewardId) { ConcurrentHashMap() }[HistoryType.DAILY] = daily
                entry.rewardHistory.getOrPut(crateRewardId) { ConcurrentHashMap() }[HistoryType.WEEKLY] = weekly
                entry.rewardHistory.getOrPut(crateRewardId) { ConcurrentHashMap() }[HistoryType.MONTHLY] = monthly
            }
        })
    }

    fun loadGlobalHistory(): Pair<ConcurrentHashMap<String, ConcurrentHashMap<HistoryType, Int>>, ConcurrentHashMap<String, ConcurrentHashMap<HistoryType, Int>>> {
        // CrateId, Daily/Weekly/Monthly/Alltime, Amount
        val openHistory = ConcurrentHashMap<String, ConcurrentHashMap<HistoryType, Int>>()

        // CrateId:RewardId, Daily/Weekly/Monthly/Alltime, Amount
        val rewardHistory = ConcurrentHashMap<String, ConcurrentHashMap<HistoryType, Int>>()

        @Language("SQL")
        val crateHistorySql = "SELECT crate_id, " +
                "       COUNT(*) AS all_time, " +
                "       SUM(CASE WHEN open_timestamp >= strftime('%s', 'now', '-1 day') THEN 1 ELSE 0 END) AS daily, " +
                "       SUM(CASE WHEN open_timestamp >= strftime('%s', 'now', '-7 days') THEN 1 ELSE 0 END) AS weekly, " +
                "       SUM(CASE WHEN open_timestamp >= strftime('%s', 'now', '-1 month') THEN 1 ELSE 0 END) AS monthly " +
                "FROM aquaticcrates_opens " +
                "GROUP BY crate_id;"

        driver.executeQuery(crateHistorySql, { }, {
            while (next()) {
                val crateId = getString("crate_id")
                val allTime = getInt("all_time")
                val daily = getInt("daily")
                val weekly = getInt("weekly")
                val monthly = getInt("monthly")
                openHistory.getOrPut(crateId) { ConcurrentHashMap() }[HistoryType.ALLTIME] = allTime
                openHistory.getOrPut(crateId) { ConcurrentHashMap() }[HistoryType.DAILY] = daily
                openHistory.getOrPut(crateId) { ConcurrentHashMap() }[HistoryType.WEEKLY] = weekly
                openHistory.getOrPut(crateId) { ConcurrentHashMap() }[HistoryType.MONTHLY] = monthly
            }
        })

        @Language("SQL")
        val rewardHistorySql = "SELECT o.crate_id || ':' || r.reward_id AS crate_reward_id, " +
                "       COUNT(*) AS all_time, " +
                "       SUM(CASE WHEN o.open_timestamp >= strftime('%s', 'now', '-1 day') THEN 1 ELSE 0 END) AS daily, " +
                "       SUM(CASE WHEN o.open_timestamp >= strftime('%s', 'now', '-7 days') THEN 1 ELSE 0 END) AS weekly, " +
                "       SUM(CASE WHEN o.open_timestamp >= strftime('%s', 'now', '-1 month') THEN 1 ELSE 0 END) AS monthly " +
                "FROM aquaticcrates_opens o " +
                "         JOIN aquaticcrates_rewards r ON o.id = r.open_id " +
                "GROUP BY crate_reward_id;"
        driver.executeQuery(rewardHistorySql, { }, {
            while (next()) {
                val crateRewardId = getString("crate_reward_id")
                val allTime = getInt("all_time")
                val daily = getInt("daily")
                val weekly = getInt("weekly")
                val monthly = getInt("monthly")
                rewardHistory.getOrPut(crateRewardId) { ConcurrentHashMap() }[HistoryType.ALLTIME] = allTime
                rewardHistory.getOrPut(crateRewardId) { ConcurrentHashMap() }[HistoryType.DAILY] = daily
                rewardHistory.getOrPut(crateRewardId) { ConcurrentHashMap() }[HistoryType.WEEKLY] = weekly
                rewardHistory.getOrPut(crateRewardId) { ConcurrentHashMap() }[HistoryType.MONTHLY] = monthly
            }
        })
        return openHistory to rewardHistory
    }
}