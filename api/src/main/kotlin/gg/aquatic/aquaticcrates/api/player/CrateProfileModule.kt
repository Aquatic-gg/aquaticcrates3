package gg.aquatic.aquaticcrates.api.player

import gg.aquatic.aquaticcrates.api.reward.RewardContainer
import gg.aquatic.waves.profile.AquaticPlayer
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
                    "amount INT NOT NULL, " +
                    "PRIMARY KEY (id, key_id), " +
                    "FOREIGN KEY (id) REFERENCES aquaticprofiles(id)" +
                    ")"
        ).use {
            it.execute()
        }
    }

    override fun loadEntry(player: AquaticPlayer): ProfileModuleEntry {
        return CrateProfileDriver.get(player)
    }
}

fun AquaticPlayer.crateEntry(): CrateProfileEntry {
    return this.entries.getOrPut(CrateProfileModule.id) { CrateProfileEntry(this, RewardContainer()) } as CrateProfileEntry
}