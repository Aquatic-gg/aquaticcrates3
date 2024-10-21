package gg.aquatic.aquaticcrates.plugin

import gg.aquatic.aquaticcrates.api.AbstractCratesPlugin
import gg.aquatic.aquaticcrates.api.player.CrateProfileModule
import gg.aquatic.aquaticseries.lib.util.await
import gg.aquatic.waves.profile.ProfilesModule

class CratesPlugin: AbstractCratesPlugin() {

    companion object {
        val INSTANCE: AbstractCratesPlugin
            get() {
                return AbstractCratesPlugin.INSTANCE
            }
    }

    override fun onLoad() {
        AbstractCratesPlugin.INSTANCE = this
    }

    override fun onEnable() {
        await {
            ProfilesModule.registerModule(CrateProfileModule)
        }
    }

}