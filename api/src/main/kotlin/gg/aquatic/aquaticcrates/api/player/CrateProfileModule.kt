package gg.aquatic.aquaticcrates.api.player

import gg.aquatic.aquaticcrates.api.reward.RewardContainer
import gg.aquatic.waves.data.MySqlDriver
import gg.aquatic.waves.profile.AquaticPlayer
import gg.aquatic.waves.profile.ProfilesModule
import gg.aquatic.waves.profile.module.ProfileModule
import gg.aquatic.waves.profile.module.ProfileModuleEntry
import java.sql.Connection

object CrateProfileModule: ProfileModule {
    override val id: String = "aquaticcrates_profile_module"

    override fun initialize(connection: Connection) {
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


    }

    override fun loadEntry(player: AquaticPlayer): ProfileModuleEntry {
        return CrateProfileDriver.get(player)
    }
}

fun AquaticPlayer.crateEntry(): CrateProfileEntry {
    return this.entries.getOrPut(CrateProfileModule.id) { CrateProfileEntry(this, RewardContainer()) } as CrateProfileEntry
}