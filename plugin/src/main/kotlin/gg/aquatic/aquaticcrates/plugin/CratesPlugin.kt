package gg.aquatic.aquaticcrates.plugin

import gg.aquatic.aquaticcrates.api.AbstractCratesPlugin
import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import gg.aquatic.aquaticcrates.api.player.CrateProfileModule
import gg.aquatic.aquaticcrates.plugin.serialize.PouchSerializer
import gg.aquatic.aquaticseries.lib.util.await
import gg.aquatic.aquaticseries.lib.util.runLaterAsync
import gg.aquatic.aquaticseries.lib.util.runLaterSync
import gg.aquatic.waves.profile.ProfilesModule
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File

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
        runLaterAsync(1) {
            load()
        }
    }

    private fun load() {
        runBlocking {
            launch {
                ProfilesModule.registerModule(CrateProfileModule)

                val pouchFile = File(dataFolder,"pouches")
                pouchFile.mkdirs()
                CrateHandler.pouches += PouchSerializer.loadPouches(pouchFile)
            }
        }
    }

}