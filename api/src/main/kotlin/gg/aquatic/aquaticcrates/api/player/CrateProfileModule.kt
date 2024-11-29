package gg.aquatic.aquaticcrates.api.player

import gg.aquatic.aquaticcrates.api.reward.RewardContainer
import gg.aquatic.waves.profile.AquaticPlayer
import gg.aquatic.waves.profile.module.ProfileModule
import gg.aquatic.waves.profile.module.ProfileModuleEntry
import java.sql.Connection

object CrateProfileModule: ProfileModule {
    override val id: String = "aquaticcrates_profile_module"

    override fun initialize(connection: Connection) {

    }

    override fun loadEntry(player: AquaticPlayer): ProfileModuleEntry {
        return CrateProfileEntry(
            player,
            RewardContainer()
        )
    }
}

fun AquaticPlayer.crateEntry(): CrateProfileEntry {
    return this.entries[CrateProfileModule.id] as CrateProfileEntry
}