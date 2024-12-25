package gg.aquatic.aquaticcrates.plugin

import gg.aquatic.aquaticcrates.api.AbstractCratesPlugin
import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.player.CrateProfileModule
import gg.aquatic.aquaticcrates.api.reroll.RerollManager
import gg.aquatic.aquaticcrates.plugin.animation.action.SetBlockAction
import gg.aquatic.aquaticcrates.plugin.animation.action.bossbar.*
import gg.aquatic.aquaticcrates.plugin.animation.action.entity.ShowEntityAction
import gg.aquatic.aquaticcrates.plugin.animation.action.entity.UpdateEntityPropertiesAction
import gg.aquatic.aquaticcrates.plugin.animation.action.model.HideModelAction
import gg.aquatic.aquaticcrates.plugin.animation.action.model.PlayModelAnimationAction
import gg.aquatic.aquaticcrates.plugin.animation.action.model.ShowModelAction
import gg.aquatic.aquaticcrates.plugin.animation.action.path.LinearPathAction
import gg.aquatic.aquaticcrates.plugin.command.CrateCommand
import gg.aquatic.aquaticcrates.plugin.command.KeyCommand
import gg.aquatic.aquaticcrates.plugin.interact.action.*
import gg.aquatic.aquaticcrates.plugin.reroll.input.inventory.InventoryRerollInput
import gg.aquatic.aquaticcrates.plugin.reroll.input.inventory.RerollMenu
import gg.aquatic.aquaticcrates.plugin.restriction.impl.*
import gg.aquatic.aquaticcrates.plugin.serialize.CrateSerializer
import gg.aquatic.waves.command.AquaticBaseCommand
import gg.aquatic.waves.command.register
import gg.aquatic.waves.inventory.event.AsyncPacketInventoryCloseEvent
import gg.aquatic.waves.profile.ProfilesModule
import gg.aquatic.waves.registry.WavesRegistry
import gg.aquatic.waves.registry.registerAction
import gg.aquatic.waves.registry.registerRequirement
import gg.aquatic.waves.util.Config
import gg.aquatic.waves.util.event.event
import gg.aquatic.waves.util.runAsyncTimer
import gg.aquatic.waves.util.runSyncTimer
import org.bukkit.event.world.WorldLoadEvent
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.runAsync

class CratesPlugin : AbstractCratesPlugin() {

    companion object {
        val INSTANCE: AbstractCratesPlugin
            get() {
                return AbstractCratesPlugin.INSTANCE
            }
        lateinit var spawnedCratesConfig: Config
    }

    override fun onLoad() {
        AbstractCratesPlugin.INSTANCE = this
        spawnedCratesConfig = Config("spawnedcrates.yml", INSTANCE)
    }

    var loading = true
        private set

    override fun onEnable() {
        registerObjects()
        ProfilesModule.registerModule(CrateProfileModule)
        load()

        event<WorldLoadEvent> {
            CrateHandler.onWorldLoad(it.world)
        }

        startTicker()
        AquaticBaseCommand(
            "aquaticcrates",
            "Base command of AquaticCrates plugin",
            mutableListOf(
                "acrates"
            ),
            mutableMapOf(
                "key" to KeyCommand,
                "crate" to CrateCommand
            ),
            listOf()
        ).register("aquaticcrates")

        event<AsyncPacketInventoryCloseEvent> {
            val inv = it.inventory
            if (inv !is RerollMenu) return@event
            if (inv.future.isDone) return@event
            when(inv.settings.onClose) {
                InventoryRerollInput.Action.CANCEL -> {
                    inv.open()
                }
                InventoryRerollInput.Action.REROLL -> {
                    inv.future.complete(RerollManager.RerollResult(true))
                }
                else -> {
                    inv.future.complete(RerollManager.RerollResult(false))
                }
            }
        }
    }

    private fun startTicker() {
        runAsyncTimer(1, 1) {
            for ((_, crate) in CrateHandler.crates) {
                if (crate is OpenableCrate) {
                    crate.animationManager.tick()
                }
            }
        }
    }

    private fun load(): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        loading = true
        runAsync {
            CrateHandler.crates += CrateSerializer.loadCrates()
            CrateHandler.loadSpawnedCrates(spawnedCratesConfig)
            future.complete(null)
            loading = false
        }
        return future
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
        WavesRegistry.registerAction("show-model", ShowModelAction())
        WavesRegistry.registerAction("play-model-animation", PlayModelAnimationAction())
        WavesRegistry.registerAction("hide-model", HideModelAction())

        WavesRegistry.registerAction("open-crate", CrateOpenAction())
        WavesRegistry.registerAction("open-crate-instant", CrateInstantOpenAction())
        WavesRegistry.registerAction("preview-crate", CratePreviewAction())
        WavesRegistry.registerAction("destroy-crate", CrateBreakAction())
        WavesRegistry.registerAction("execute-actions", CrateExecuteActionsAction())

        WavesRegistry.registerRequirement("player", PlayerOpenRestriction())
        WavesRegistry.registerRequirement("global", GlobalOpenRestriction())
        WavesRegistry.registerRequirement("all_player", AllPlayerOpenRestriction())
        WavesRegistry.registerRequirement("all_global", AllGlobalOpenRestriction())
        WavesRegistry.registerRequirement("world_blacklist", WorldBlacklistOpenRestriction())

    }
}