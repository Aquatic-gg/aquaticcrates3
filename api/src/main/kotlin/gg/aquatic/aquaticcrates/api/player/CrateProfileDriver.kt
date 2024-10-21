package gg.aquatic.aquaticcrates.api.player

import gg.aquatic.aquaticseries.lib.data.DataDriver
import gg.aquatic.waves.Waves
import gg.aquatic.waves.module.WaveModules
import gg.aquatic.waves.profile.ProfilesModule

object CrateProfileDriver {
    val driver: DataDriver = (Waves.INSTANCE.modules[WaveModules.PROFILES] as ProfilesModule).driver
}