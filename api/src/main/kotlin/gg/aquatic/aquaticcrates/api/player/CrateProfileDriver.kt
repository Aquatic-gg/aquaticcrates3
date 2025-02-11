package gg.aquatic.aquaticcrates.api.player

import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import gg.aquatic.aquaticcrates.api.player.CrateProfileEntry.HistoryType
import gg.aquatic.aquaticcrates.api.reward.RewardContainer
import gg.aquatic.waves.Waves
import gg.aquatic.waves.data.DataDriver
import gg.aquatic.waves.data.MySqlDriver
import gg.aquatic.waves.module.WaveModules
import gg.aquatic.waves.profile.AquaticPlayer
import gg.aquatic.waves.profile.ProfilesModule
import gg.aquatic.waves.util.item.decodeToItemStack
import gg.aquatic.waves.util.item.encode
import org.intellij.lang.annotations.Language
import java.sql.Connection
import java.sql.PreparedStatement
import java.util.concurrent.ConcurrentHashMap

object CrateProfileDriver {
    val driver: DataDriver = (Waves.INSTANCE.modules[WaveModules.PROFILES] as ProfilesModule).driver

    fun isSQLite(): Boolean {
        return driver is MySqlDriver
    }

    fun isMySQL(): Boolean {
        return !isSQLite()
    }

    fun get(aquaticPlayer: AquaticPlayer): CrateProfileEntry {
        val entry = CrateProfileEntry(aquaticPlayer, RewardContainer())
        loadKeys(entry)
        loadHistory(entry)
        loadRewardContainer(entry)
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
        saveRewardContainer(connection, entry)
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

    private fun saveRewardContainer(connection: Connection, entry: CrateProfileEntry) {
        try {
            val isSQLite = isSQLite()

            // SQL to insert a new item into the `items` table
            val insertItemSql = if (isSQLite) {
                "INSERT OR IGNORE INTO items (item_data) VALUES (?)"
            } else {
                "INSERT IGNORE INTO items (item_data) VALUES (?)"
            }

            // SQL to retrieve the `item_id` of an item
            val getItemIdSql = "SELECT item_id FROM items WHERE item_data = ?"

            // SQL to insert or update the player's item quantities
            val insertOrUpdatePlayerItemSql = if (isSQLite) {
                "INSERT OR REPLACE INTO player_items (player_id, item_id, quantity) VALUES (?, ?, ?)"
            } else {
                """
            INSERT INTO player_items (player_id, item_id, quantity)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE quantity = VALUES(quantity)
            """
            }

            // Serialize all items in the RewardContainer
            val rewardContainer = entry.rewardContainer
            val serializedItems = rewardContainer.items.mapKeys { (itemStack, _) ->
                itemStack.encode()
            }

            // Process statements
            connection.prepareStatement(insertItemSql).use { insertItemStmt ->
                connection.prepareStatement(getItemIdSql).use { getItemIdStmt ->
                    connection.prepareStatement(insertOrUpdatePlayerItemSql).use { playerItemStmt ->
                        serializedItems.forEach { (serializedItem, quantity) ->
                            // Step 1: Insert item into the items table, if it doesn’t already exist
                            insertItemStmt.setString(1, serializedItem)
                            insertItemStmt.executeUpdate()

                            // Step 2: Retrieve the `item_id` from the items table
                            var itemId: Int? = null
                            getItemIdStmt.setString(1, serializedItem)
                            getItemIdStmt.executeQuery().use { resultSet ->
                                if (resultSet.next()) {
                                    itemId = resultSet.getInt("item_id")
                                }
                            }

                            // Step 3: Insert or update the `player_items` table
                            itemId?.let {
                                playerItemStmt.setInt(1, entry.aquaticPlayer.index) // Using the player's ID
                                playerItemStmt.setInt(2, it)
                                playerItemStmt.setInt(3, quantity)
                                playerItemStmt.executeUpdate()
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    internal fun saveHistory(connection: Connection, profileEntry: CrateProfileEntry) {
        val insertOpensQuery = "INSERT INTO aquaticcrates_opens (user_id, open_timestamp, crate_id) VALUES (?, ?, ?);"
        val insertRewardsQuery = "INSERT INTO aquaticcrates_rewards (open_id, reward_id, amount) VALUES (?, ?, ?);"

        // Collect auto-generated open IDs and their related rewards
        val openIdToRewards = mutableListOf<Pair<Int, List<Pair<String, Int>>>>()

        // Insert into `aquaticcrates_opens` and retrieve auto-generated keys
        connection.prepareStatement(insertOpensQuery, PreparedStatement.RETURN_GENERATED_KEYS).use { opensStmt ->
            for ((_, entries) in profileEntry.newEntries) {
                for (entry in entries) {
                    // Prepare batch for "opens" table inserts
                    opensStmt.setInt(1, profileEntry.aquaticPlayer.index) // Assuming userId is numeric in string form
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
                        val rewards =
                            entry.rewardIds.map { it.key to it.value } // Convert rewardIds into list of (rewardId, amount)
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

    fun loadRewardContainer(entry: CrateProfileEntry) {
        try {
            val query = """
            SELECT i.item_data, pi.quantity
            FROM player_items pi
            JOIN items i ON pi.item_id = i.item_id
            WHERE pi.player_id = ?
        """

            driver.useConnection {
                prepareStatement(query).use { statement ->
                    // Set the player ID in the query
                    statement.setInt(1, entry.aquaticPlayer.index) // `aquaticPlayer.id` identifies the player

                    // Execute the query
                    statement.executeQuery().use { resultSet ->
                        while (resultSet.next()) {
                            // Retrieve `item_data` (serialized ItemStack) and `quantity`
                            val serializedItem = resultSet.getString("item_data")
                            val quantity = resultSet.getInt("quantity")

                            // Deserialize the `item_data` back into an ItemStack
                            val itemStack = serializedItem.decodeToItemStack()

                            // Add the item to the RewardContainer's `rewards` map
                            entry.rewardContainer.items[itemStack] = quantity
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace() // Log or handle the exception as needed
        }

    }

    fun loadHistory(entry: CrateProfileEntry) {
        @Language("SQL")
        val crateHistorySql = if (driver is MySqlDriver)
            "SELECT crate_id, " +
                    "       COUNT(*) AS all_time, " +
                    "       SUM(CASE WHEN open_timestamp >= UNIX_TIMESTAMP(DATE_SUB(NOW(), INTERVAL 1 DAY)) THEN 1 ELSE 0 END) AS daily, " +
                    "       SUM(CASE WHEN open_timestamp >= UNIX_TIMESTAMP(DATE_SUB(NOW(), INTERVAL 7 DAY)) THEN 1 ELSE 0 END) AS weekly, " +
                    "       SUM(CASE WHEN open_timestamp >= UNIX_TIMESTAMP(DATE_SUB(NOW(), INTERVAL 1 MONTH)) THEN 1 ELSE 0 END) AS monthly " +
                    "FROM aquaticcrates_opens " +
                    "GROUP BY crate_id;"
        else
            "SELECT crate_id, " +
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
        val rewardHistorySql = if (driver is MySqlDriver)
            "SELECT CONCAT(o.crate_id, ':', r.reward_id) AS crate_reward_id, " +
                    "       COUNT(*) AS all_time, " +
                    "       SUM(CASE WHEN o.open_timestamp >= UNIX_TIMESTAMP(DATE_SUB(NOW(), INTERVAL 1 DAY)) THEN 1 ELSE 0 END) AS daily, " +
                    "       SUM(CASE WHEN o.open_timestamp >= UNIX_TIMESTAMP(DATE_SUB(NOW(), INTERVAL 7 DAY)) THEN 1 ELSE 0 END) AS weekly, " +
                    "       SUM(CASE WHEN o.open_timestamp >= UNIX_TIMESTAMP(DATE_SUB(NOW(), INTERVAL 1 MONTH)) THEN 1 ELSE 0 END) AS monthly " +
                    "FROM aquaticcrates_opens o " +
                    "         JOIN aquaticcrates_rewards r ON o.id = r.open_id " +
                    "GROUP BY crate_reward_id;"
        else "SELECT o.crate_id || ':' || r.reward_id AS crate_reward_id, " +
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
        val crateHistorySql = if (driver is MySqlDriver) "SELECT crate_id, " +
                "       COUNT(*) AS all_time, " +
                "       SUM(CASE WHEN open_timestamp >= UNIX_TIMESTAMP(DATE_SUB(NOW(), INTERVAL 1 DAY)) THEN 1 ELSE 0 END) AS daily, " +
                "       SUM(CASE WHEN open_timestamp >= UNIX_TIMESTAMP(DATE_SUB(NOW(), INTERVAL 7 DAY)) THEN 1 ELSE 0 END) AS weekly, " +
                "       SUM(CASE WHEN open_timestamp >= UNIX_TIMESTAMP(DATE_SUB(NOW(), INTERVAL 1 MONTH)) THEN 1 ELSE 0 END) AS monthly " +
                "FROM aquaticcrates_opens " +
                "GROUP BY crate_id;"
        else "SELECT crate_id, " +
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
        val rewardHistorySql =
            if (driver is MySqlDriver) "SELECT CONCAT(o.crate_id, ':', r.reward_id) AS crate_reward_id, " +
                    "       COUNT(*) AS all_time, " +
                    "       SUM(CASE WHEN o.open_timestamp >= UNIX_TIMESTAMP(DATE_SUB(NOW(), INTERVAL 1 DAY)) THEN 1 ELSE 0 END) AS daily, " +
                    "       SUM(CASE WHEN o.open_timestamp >= UNIX_TIMESTAMP(DATE_SUB(NOW(), INTERVAL 7 DAY)) THEN 1 ELSE 0 END) AS weekly, " +
                    "       SUM(CASE WHEN o.open_timestamp >= UNIX_TIMESTAMP(DATE_SUB(NOW(), INTERVAL 1 MONTH)) THEN 1 ELSE 0 END) AS monthly " +
                    "FROM aquaticcrates_opens o " +
                    "         JOIN aquaticcrates_rewards r ON o.id = r.open_id " +
                    "GROUP BY crate_reward_id;"
            else "SELECT o.crate_id || ':' || r.reward_id AS crate_reward_id, " +
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

    fun loadLogEntries(
        offset: Int,
        limit: Int,
        playerName: String?,
        crateId: String?,
        sorting: Sorting?,
    ): MutableMap<Int, Pair<String, CrateProfileEntry.OpenHistoryEntry>> {
        val resultMap: MutableMap<Int, Pair<String, CrateProfileEntry.OpenHistoryEntry>> = mutableMapOf()

        try {
            // Construct the query dynamically based on nullable parameters
            val queryBuilder = StringBuilder(
                """
            SELECT o.id AS open_id, o.user_id, o.open_timestamp, o.crate_id, r.reward_id, r.amount, p.username
            FROM aquaticcrates_opens o
            LEFT JOIN aquaticcrates_rewards r ON o.id = r.open_id
            LEFT JOIN aquaticprofiles p ON o.user_id = p.id
            """.trimIndent()
            )

            // Add WHERE conditions based on the provided filters
            val conditions = mutableListOf<String>()
            if (playerName != null) {
                conditions.add("p.username = ?")
            }
            if (crateId != null) {
                conditions.add("o.crate_id = ?")
            }

            if (conditions.isNotEmpty()) {
                queryBuilder.append(" WHERE ").append(conditions.joinToString(" AND "))
            }

            // Add sorting based on the Sorting enum (NEWEST or OLDEST)
            when (sorting) {
                Sorting.NEWEST -> queryBuilder.append(" ORDER BY o.open_timestamp DESC")
                Sorting.OLDEST -> queryBuilder.append(" ORDER BY o.open_timestamp ASC")
                else -> queryBuilder.append(" ORDER BY o.open_timestamp DESC") // Default to NEWEST
            }

            // Add pagination with LIMIT and OFFSET
            queryBuilder.append(" LIMIT ? OFFSET ?")

            // Prepare the statement and bind parameters
            driver.useConnection {
                prepareStatement(queryBuilder.toString()).use { statement ->
                    var parameterIndex = 1

                    // Bind the playerName if provided
                    if (playerName != null) {
                        statement.setString(parameterIndex++, playerName)
                    }

                    // Bind the crateId if provided
                    if (crateId != null) {
                        statement.setString(parameterIndex++, crateId)
                    }

                    // Bind the limit and offset for pagination
                    statement.setInt(parameterIndex++, limit)
                    statement.setInt(parameterIndex, offset)

                    // Execute the query
                    val resultSet = statement.executeQuery()

                    // Populate the resultMap
                    while (resultSet.next()) {
                        val openId = resultSet.getInt("open_id")
                        val timestamp = resultSet.getLong("open_timestamp")
                        val crateIdResult = resultSet.getString("crate_id")
                        val rewardId = resultSet.getString("reward_id")
                        val rewardAmount = resultSet.getInt("amount")
                        val username = resultSet.getString("username")

                        // Check if an OpenHistoryEntry already exists for this `openId`
                        val existingEntry = resultMap[openId]

                        if (existingEntry != null) {
                            // Update reward information in the existing OpenHistoryEntry
                            val updatedEntry = existingEntry.second
                            updatedEntry.rewardIds[rewardId] = (updatedEntry.rewardIds[rewardId] ?: 0) + rewardAmount
                        } else {
                            // Create a new OpenHistoryEntry and add it to the map
                            val newEntry = CrateProfileEntry.OpenHistoryEntry(
                                timestamp = timestamp,
                                crateId = crateIdResult,
                                rewardIds = hashMapOf(rewardId to rewardAmount)
                            )
                            resultMap[openId] = Pair(username, newEntry)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return resultMap
    }

    enum class Sorting {
        NEWEST, OLDEST
    }

}