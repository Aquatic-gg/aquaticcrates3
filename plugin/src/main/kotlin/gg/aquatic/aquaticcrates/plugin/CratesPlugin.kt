package gg.aquatic.aquaticcrates.plugin

import gg.aquatic.aquaticcrates.api.AbstractCratesPlugin
import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import gg.aquatic.aquaticcrates.api.player.CrateProfileModule
import gg.aquatic.aquaticcrates.plugin.animation.action.SetBlockAction
import gg.aquatic.aquaticcrates.plugin.animation.action.bossbar.*
import gg.aquatic.aquaticcrates.plugin.animation.action.entity.ShowEntityAction
import gg.aquatic.aquaticcrates.plugin.animation.action.entity.UpdateEntityPropertiesAction
import gg.aquatic.aquaticcrates.plugin.animation.action.path.LinearPathAction
import gg.aquatic.aquaticcrates.plugin.serialize.PouchSerializer
import gg.aquatic.aquaticseries.lib.util.*
import gg.aquatic.waves.profile.ProfilesModule
import gg.aquatic.waves.registry.WavesRegistry
import gg.aquatic.waves.registry.registerAction
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
        registerObjects()
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

    private fun registerObjects() {
        WavesRegistry.registerAction("show-bossbar", ShowBossbarAction())
        WavesRegistry.registerAction("hide-bossbar", HideBossbarAction())
        WavesRegistry.registerAction("set-bossbar-message", SetBossbarTextAction())
        WavesRegistry.registerAction("set-bossbar-color", SetBossbarColorAction())
        WavesRegistry.registerAction("set-bossbar-style", SetBossbarStyleAction())
        WavesRegistry.registerAction("set-bossbar-progress", SetBossbarProgressAction())
        WavesRegistry.registerAction("linear-path", LinearPathAction())
        WavesRegistry.registerAction("show-entity", ShowEntityAction())
        WavesRegistry.registerAction("update-entity-properties", UpdateEntityPropertiesAction())
        WavesRegistry.registerAction("set-block", SetBlockAction())
    }
}