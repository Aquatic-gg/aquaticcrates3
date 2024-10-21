package gg.aquatic.aquaticcrates.api.player

import gg.aquatic.waves.profile.AquaticPlayer
import gg.aquatic.waves.profile.module.ProfileModule
import gg.aquatic.waves.profile.module.ProfileModuleEntry
import java.sql.Connection

class CrateProfileModule: ProfileModule {
    override val id: String = "aquaticcrates_profile_module"

    override fun initialize(connection: Connection) {

    }

    override suspend fun loadEntry(player: AquaticPlayer): ProfileModuleEntry {
        TODO("Not yet implemented")
    }
}