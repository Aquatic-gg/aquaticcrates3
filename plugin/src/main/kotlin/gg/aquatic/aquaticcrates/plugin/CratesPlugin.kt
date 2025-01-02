package gg.aquatic.aquaticcrates.plugin

import gg.aquatic.aquaticcrates.api.AbstractCratesPlugin
import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.player.CrateProfileModule
import gg.aquatic.aquaticcrates.api.reroll.RerollManager
import gg.aquatic.aquaticcrates.plugin.animation.action.ConditionalActionsAction
import gg.aquatic.aquaticcrates.plugin.animation.action.SoundAction
import gg.aquatic.aquaticcrates.plugin.animation.action.StartTickerAction
import gg.aquatic.aquaticcrates.plugin.animation.action.block.SetBlockAction
import gg.aquatic.aquaticcrates.plugin.animation.action.block.SetMultiblockAction
import gg.aquatic.aquaticcrates.plugin.animation.action.bossbar.*
import gg.aquatic.aquaticcrates.plugin.animation.action.entity.ShowEntityAction
import gg.aquatic.aquaticcrates.plugin.animation.action.entity.UpdateEntityPropertiesAction
import gg.aquatic.aquaticcrates.plugin.animation.action.model.HideModelAction
import gg.aquatic.aquaticcrates.plugin.animation.action.model.PlayModelAnimationAction
import gg.aquatic.aquaticcrates.plugin.animation.action.model.ShowModelAction
import gg.aquatic.aquaticcrates.plugin.animation.action.path.LinearPathAction
import gg.aquatic.aquaticcrates.plugin.animation.condition.CustomCondition
import gg.aquatic.aquaticcrates.plugin.awaiters.AbstractAwaiter
import gg.aquatic.aquaticcrates.plugin.awaiters.IAAwaiter
import gg.aquatic.aquaticcrates.plugin.awaiters.MEGAwaiter
import gg.aquatic.aquaticcrates.plugin.command.CrateCommand
import gg.aquatic.aquaticcrates.plugin.command.KeyCommand
import gg.aquatic.aquaticcrates.plugin.command.ReloadCommand
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
import org.bukkit.Bukkit
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

        val awaiters = mutableListOf<AbstractAwaiter>()
        if (Bukkit.getPluginManager().getPlugin("ModelEngine") != null) {
            val awaiter = MEGAwaiter()
            awaiters += awaiter
            awaiter.future.thenRun {
                awaiters -= awaiter
                if (awaiters.isEmpty()) {
                    load()
                }
            }
        }
        if (Bukkit.getPluginManager().getPlugin("ItemsAdder") != null) {
            val awaiter = IAAwaiter()
            awaiters += awaiter
            awaiter.future.thenRun {
                awaiters -= awaiter
                if (awaiters.isEmpty()) {
                    load()
                }
            }
        }
        if (awaiters.isEmpty()) {
            load()
        }
        
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
                "crate" to CrateCommand,
                "reload" to ReloadCommand
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

    fun reloadPlugin(): CompletableFuture<Boolean> {
        if (loading) {
            return CompletableFuture.completedFuture(false)
        }
        for (value in CrateHandler.crates.values) {
            if (value is OpenableCrate) {
                value.animationManager.forceStopAnimations()
            }
        }
        CrateHandler.crates.clear()
        for (value in CrateHandler.spawned.values) {
            value.destroy()
        }
        CrateHandler.spawned.clear()
        return load().thenApply { true }
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
        loading = true
        return runAsync {
            CrateHandler.crates += CrateSerializer.loadCrates()
            CrateHandler.loadSpawnedCrates(spawnedCratesConfig)
            loading = false
        }.exceptionally {
            it.printStackTrace()
            null
        }
    }

    private fun registerObjects() {
        // Animation Actions
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
        WavesRegistry.registerAction("set-multiblock", SetMultiblockAction())
        WavesRegistry.registerAction("show-model", ShowModelAction())
        WavesRegistry.registerAction("play-model-animation", PlayModelAnimationAction())
        WavesRegistry.registerAction("hide-model", HideModelAction())
        WavesRegistry.registerAction("play-sound", SoundAction())
        WavesRegistry.registerAction("conditional-actions", ConditionalActionsAction())
        WavesRegistry.registerAction("start-ticker", StartTickerAction())

        // Interaction Actions
        WavesRegistry.registerAction("open-crate", CrateOpenAction())
        WavesRegistry.registerAction("open-crate-instant", CrateInstantOpenAction())
        WavesRegistry.registerAction("preview-crate", CratePreviewAction())
        WavesRegistry.registerAction("destroy-crate", CrateBreakAction())
        WavesRegistry.registerAction("execute-actions", CrateExecuteActionsAction())

        // Open Restrictions
        WavesRegistry.registerRequirement("player", PlayerOpenRestriction())
        WavesRegistry.registerRequirement("global", GlobalOpenRestriction())
        WavesRegistry.registerRequirement("all_player", AllPlayerOpenRestriction())
        WavesRegistry.registerRequirement("all_global", AllGlobalOpenRestriction())
        WavesRegistry.registerRequirement("world_blacklist", WorldBlacklistOpenRestriction())

        // Animation Action Conditions
        WavesRegistry.registerRequirement("custom", CustomCondition())

    }
}