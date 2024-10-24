package gg.aquatic.aquaticcrates.plugin

import gg.aquatic.aquaticcrates.api.AbstractCratesPlugin
import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.player.CrateProfileModule
import gg.aquatic.aquaticcrates.plugin.serialize.PouchSerializer
import gg.aquatic.aquaticseries.lib.util.*
import gg.aquatic.waves.profile.ProfilesModule
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.bukkit.event.player.PlayerJoinEvent
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
        load()

        event<PlayerJoinEvent> {
            for (value in CrateHandler.pouches.values) {
                value.giveItem(1, it.player)
                it.player.sendMessage("You have been given ${value.identifier} pouch!")
            }
        }
        startTicker()
    }

    private fun startTicker() {
        runSyncTimer(1,1) {
            for (value in CrateHandler.pouches.values) {
                value.animationManager.tick()
            }
            for (value in CrateHandler.crates.values) {
            }
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