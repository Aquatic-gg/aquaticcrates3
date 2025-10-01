package gg.aquatic.aquaticcrates.api.player

import gg.aquatic.aquaticcrates.api.reward.RewardContainer
import gg.aquatic.waves.data.MySqlDriver
import gg.aquatic.waves.profile.AquaticPlayer
import gg.aquatic.waves.profile.ProfilesModule
import gg.aquatic.waves.profile.module.ProfileModule
import gg.aquatic.waves.profile.module.ProfileModuleEntry
import java.sql.Connection
import java.util.concurrent.atomic.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi

object CrateProfileModule: ProfileModule {
    override val id: String = "aquaticcrates_profile_module"

    @OptIn(ExperimentalAtomicApi::class)
    override fun initialize(connection: Connection) {
        if (CrateProfileDriver.isSQLite()) {
            val enableForeignKeys = "PRAGMA foreign_keys = ON;"
            connection.createStatement().execute(enableForeignKeys)
        }

        connection.prepareStatement(if (CrateProfileDriver.isSQLite()) {
            """
            CREATE TABLE IF NOT EXISTS items (
                item_id INTEGER PRIMARY KEY AUTOINCREMENT, -- Auto-increment for SQLite
                item_data BLOB NOT NULL, -- Serialized data of the ItemStack (JSON or binary)
                UNIQUE(item_data) -- Ensure no duplicate items
            );
            """
        } else {
            """
            CREATE TABLE IF NOT EXISTS items (
                item_id INT AUTO_INCREMENT PRIMARY KEY, -- Auto-increment for MySQL
                item_data BLOB NOT NULL, -- Serialized data of the ItemStack (JSON or binary)
                UNIQUE(item_data) -- Ensure no duplicate items
            );
            """
        }).use {
            it.execute()
        }

        connection.prepareStatement("""
            CREATE TABLE IF NOT EXISTS player_items (
                player_id INTEGER NOT NULL,
                item_id INTEGER NOT NULL,
                quantity INTEGER NOT NULL,
                PRIMARY KEY (player_id, item_id), -- Composite primary key
                FOREIGN KEY (player_id) REFERENCES aquaticprofiles(id) ON DELETE CASCADE, -- Cascading delete
                FOREIGN KEY (item_id) REFERENCES items(item_id) ON DELETE CASCADE
            );
            """
        ).use {
            it.execute()
        }

        connection.prepareStatement(
            "CREATE TABLE IF NOT EXISTS " +
                    "aquaticcrates_keys (" +
                    "id INTEGER NOT NULL, " +
                    "key_id NVARCHAR(64) NOT NULL, " +
                    "amount INTEGER NOT NULL, " +
                    "PRIMARY KEY (id, key_id), " +
                    "FOREIGN KEY (id) REFERENCES aquaticprofiles(id)" +
                    ")"
        ).use {
            it.execute()
        }

        if (ProfilesModule.driver is MySqlDriver) {
            connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS \n" +
                        "aquaticcrates_opens (\n" +
                        "id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, \n" +
                        "user_id INTEGER NOT NULL, \n" +
                        "open_timestamp INTEGER NOT NULL, \n" +
                        "crate_id NVARCHAR(64) NOT NULL, \n" +
                        "FOREIGN KEY (user_id) REFERENCES aquaticprofiles(id)\n" +
                        ")"
            ).use {
                it.execute()
            }
        } else {
            connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS \n" +
                        "aquaticcrates_opens (\n" +
                        "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, \n" +
                        "user_id INTEGER NOT NULL, \n" +
                        "open_timestamp INTEGER NOT NULL, \n" +
                        "crate_id NVARCHAR(64) NOT NULL, \n" +
                        "FOREIGN KEY (user_id) REFERENCES aquaticprofiles(id)\n" +
                        ")"
            ).use {
                it.execute()
            }
        }

        if (ProfilesModule.driver is MySqlDriver) {
            connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS " +
                        "aquaticcrates_rewards (" +
                        "id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, " +
                        "open_id INTEGER NOT NULL, " +
                        "reward_id NVARCHAR(64) NOT NULL, " +
                        "amount INTEGER NOT NULL, " +
                        "FOREIGN KEY (open_id) REFERENCES aquaticcrates_opens(id)" +
                        ")"
            ).use {
                it.execute()
            }
        } else {
            connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS " +
                        "aquaticcrates_rewards (" +
                        "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                        "open_id INTEGER NOT NULL, " +
                        "reward_id NVARCHAR(64) NOT NULL, " +
                        "amount INTEGER NOT NULL, " +
                        "FOREIGN KEY (open_id) REFERENCES aquaticcrates_opens(id)" +
                        ")"
            ).use {
                it.execute()
            }
        }

        connection.prepareStatement("SELECT COALESCE(MAX(id), 0) FROM aquaticcrates_opens;").use {
            it.executeQuery().use { resultSet ->
                val lastId = if (resultSet.next()) resultSet.getLong(1) else 0
                CrateProfileDriver.idCounter = AtomicLong(lastId)
            }
        }


    }

    override fun loadEntry(player: AquaticPlayer): ProfileModuleEntry {
        return CrateProfileDriver.get(player)
    }
}

fun AquaticPlayer.crateEntry(): CrateProfileEntry {
    return this.entries.getOrPut(CrateProfileModule.id) { CrateProfileEntry(this, RewardContainer()) } as CrateProfileEntry
}