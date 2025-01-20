package gg.aquatic.aquaticcrates.api.player

import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import gg.aquatic.aquaticcrates.api.reward.RewardContainer
import gg.aquatic.waves.Waves
import gg.aquatic.waves.data.DataDriver
import gg.aquatic.waves.module.WaveModules
import gg.aquatic.waves.profile.AquaticPlayer
import gg.aquatic.waves.profile.ProfilesModule
import java.sql.Connection

object CrateProfileDriver {
    val driver: DataDriver = (Waves.INSTANCE.modules[WaveModules.PROFILES] as ProfilesModule).driver

    fun get(aquaticPlayer: AquaticPlayer): CrateProfileEntry {
        val entry = CrateProfileEntry(aquaticPlayer, RewardContainer())
        loadKeys(entry)
        return entry
    }

    fun loadKeys(entry: CrateProfileEntry) {
        driver.executeQuery("SELECT * FROM aquaticcrates_keys WHERE id = ?", {
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
        saveHistory(entry)
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

    internal fun saveHistory(entry: CrateProfileEntry) {
        // TODO
    }
}