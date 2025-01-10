package gg.aquatic.aquaticcrates.plugin

import gg.aquatic.aquaticcrates.api.AbstractCratesPlugin
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationManager
import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.player.CrateProfileModule
import gg.aquatic.aquaticcrates.api.reroll.RerollManager
import gg.aquatic.aquaticcrates.plugin.animation.action.*
import gg.aquatic.aquaticcrates.plugin.animation.action.block.SetBlockAction
import gg.aquatic.aquaticcrates.plugin.animation.action.block.SetMultiblockAction
import gg.aquatic.aquaticcrates.plugin.animation.action.bossbar.*
import gg.aquatic.aquaticcrates.plugin.animation.action.entity.ShowEntityAction
import gg.aquatic.aquaticcrates.plugin.animation.action.entity.UpdateEntityPropertiesAction
import gg.aquatic.aquaticcrates.plugin.animation.action.inventory.CloseInventoryAction
import gg.aquatic.aquaticcrates.plugin.animation.action.inventory.OpenInventoryAction
import gg.aquatic.aquaticcrates.plugin.animation.action.inventory.SetInventoryItemsAction
import gg.aquatic.aquaticcrates.plugin.animation.action.inventory.SetInventoryTitleAction
import gg.aquatic.aquaticcrates.plugin.animation.action.model.HideModelAction
import gg.aquatic.aquaticcrates.plugin.animation.action.model.PlayModelAnimationAction
import gg.aquatic.aquaticcrates.plugin.animation.action.model.ShowModelAction
import gg.aquatic.aquaticcrates.plugin.animation.action.path.BindPathAction
import gg.aquatic.aquaticcrates.plugin.animation.action.path.LinearPathAction
import gg.aquatic.aquaticcrates.plugin.animation.action.path.SmoothPathAction
import gg.aquatic.aquaticcrates.plugin.animation.action.potion.ClearPotionEffectsAction
import gg.aquatic.aquaticcrates.plugin.animation.action.potion.PotionEffectsAction
import gg.aquatic.aquaticcrates.plugin.animation.condition.CustomCondition
import gg.aquatic.aquaticcrates.plugin.animation.prop.inventory.AnimationMenu
import gg.aquatic.aquaticcrates.plugin.awaiters.AbstractAwaiter
import gg.aquatic.aquaticcrates.plugin.awaiters.IAAwaiter
import gg.aquatic.aquaticcrates.plugin.awaiters.MEGAwaiter
import gg.aquatic.aquaticcrates.plugin.command.CrateCommand
import gg.aquatic.aquaticcrates.plugin.command.KeyCommand
import gg.aquatic.aquaticcrates.plugin.command.ReloadCommand
import gg.aquatic.aquaticcrates.plugin.interact.action.*
import gg.aquatic.aquaticcrates.plugin.misc.Messages
import gg.aquatic.aquaticcrates.plugin.preview.CratePreviewMenu
import gg.aquatic.aquaticcrates.plugin.reroll.input.interaction.InteractionInputHandler
import gg.aquatic.aquaticcrates.plugin.reroll.input.inventory.InventoryRerollInput
import gg.aquatic.aquaticcrates.plugin.reroll.input.inventory.RerollMenu
import gg.aquatic.aquaticcrates.plugin.restriction.impl.*
import gg.aquatic.aquaticcrates.plugin.serialize.CrateSerializer
import gg.aquatic.waves.command.AquaticBaseCommand
import gg.aquatic.waves.command.register
import gg.aquatic.waves.inventory.InventoryManager
import gg.aquatic.waves.inventory.event.AsyncPacketInventoryCloseEvent
import gg.aquatic.waves.profile.ProfilesModule
import gg.aquatic.waves.registry.WavesRegistry
import gg.aquatic.waves.registry.registerAction
import gg.aquatic.waves.registry.registerRequirement
import gg.aquatic.waves.shadow.com.retrooper.packetevents.event.PacketReceiveEvent
import gg.aquatic.waves.shadow.com.retrooper.packetevents.protocol.packettype.PacketType
import gg.aquatic.waves.shadow.com.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity
import gg.aquatic.waves.util.*
import gg.aquatic.waves.util.event.event
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
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

        event<PlayerQuitEvent> {
            for (crate in CrateHandler.crates.values) {
                if (crate is OpenableCrate) {
                    crate.animationManager.forceStopAnimation(it.player)
                }
            }
        }
        event<PlayerToggleSneakEvent> {
            if (it.isSneaking) {
                var animationhandler: CrateAnimationManager? = null
                for (crate in CrateHandler.crates.values) {
                    if (crate is OpenableCrate) {
                        if (crate.animationManager.playingAnimations.containsKey(it.player.uniqueId)) {
                            animationhandler = crate.animationManager
                            break
                        }
                    }
                }
                if (animationhandler != null) {
                    animationhandler.skipAnimation(it.player)
                    InteractionInputHandler.onSneak(it)
                }

            }
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
            if (inv is AnimationMenu) {
                if (!inv.closed) inv.open()
                return@event
            }
            if (inv !is RerollMenu) return@event
            if (inv.future.isDone) return@event
            when (inv.settings.onClose) {
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

        event<InventoryInteractEvent> {
            val player = it.whoClicked as? Player ?: return@event
            val inv = InventoryManager.openedInventories[player] ?: return@event

            if (inv is AnimationMenu || inv is RerollMenu || inv is CratePreviewMenu) {
                inv.updateItems(player)
                it.isCancelled = true
                return@event
            }
        }

        packetEvent<PacketReceiveEvent> {
            if (packetType == PacketType.Play.Client.INTERACT_ENTITY) {
                val packet = WrapperPlayClientInteractEntity(this)
                val player = player() ?: return@packetEvent
                var isInAnimation = false
                for (crate in CrateHandler.crates.values) {
                    if (crate is OpenableCrate) {
                        if (crate.animationManager.playingAnimations.containsKey(player.uniqueId)) {
                            isInAnimation = true
                            break
                        }
                    }
                }
                if (isInAnimation) {
                    isCancelled = true
                    InteractionInputHandler.onInteract(this, packet)
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

        try {
            Messages.load()
            CrateHandler.crates += CrateSerializer.loadCrates()
            CrateHandler.loadSpawnedCrates(spawnedCratesConfig)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        loading = false

        return runAsync {
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
        WavesRegistry.registerAction("smooth-path", SmoothPathAction())
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
        WavesRegistry.registerAction("bind-path", BindPathAction())
        WavesRegistry.registerAction("title", TitleAction())
        WavesRegistry.registerAction("string-deobfuscation", StringDeobfuscationAction())
        WavesRegistry.registerAction("push-player", PushPlayerAction())
        WavesRegistry.registerAction("open-inventory", OpenInventoryAction())
        WavesRegistry.registerAction("set-inventory-items", SetInventoryItemsAction())
        WavesRegistry.registerAction("set-inventory-title", SetInventoryTitleAction())
        WavesRegistry.registerAction("close-inventory", CloseInventoryAction())
        WavesRegistry.registerAction("add-potion-effects", PotionEffectsAction())
        WavesRegistry.registerAction("remove-potion-effects", ClearPotionEffectsAction())

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