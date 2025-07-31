package gg.aquatic.aquaticcrates.plugin

import gg.aquatic.aquaticcrates.api.AbstractCratesPlugin
import gg.aquatic.aquaticcrates.api.PluginSettings
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationManager
import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.openprice.PRICES
import gg.aquatic.aquaticcrates.api.openprice.impl.CrateKeyPrice
import gg.aquatic.aquaticcrates.api.player.CrateProfileDriver
import gg.aquatic.aquaticcrates.api.player.CrateProfileModule
import gg.aquatic.aquaticcrates.api.player.HistoryHandler
import gg.aquatic.aquaticcrates.api.reroll.RerollManager
import gg.aquatic.aquaticcrates.plugin.animation.action.*
import gg.aquatic.aquaticcrates.plugin.animation.action.block.SetBlockAction
import gg.aquatic.aquaticcrates.plugin.animation.action.block.SetMultiblockAction
import gg.aquatic.aquaticcrates.plugin.animation.action.bossbar.*
import gg.aquatic.aquaticcrates.plugin.animation.action.entity.*
import gg.aquatic.aquaticcrates.plugin.animation.action.inventory.CloseInventoryAction
import gg.aquatic.aquaticcrates.plugin.animation.action.inventory.OpenInventoryAction
import gg.aquatic.aquaticcrates.plugin.animation.action.inventory.SetInventoryItemsAction
import gg.aquatic.aquaticcrates.plugin.animation.action.inventory.SetInventoryTitleAction
import gg.aquatic.aquaticcrates.plugin.animation.action.model.BMHideModelAction
import gg.aquatic.aquaticcrates.plugin.animation.action.model.BMPlayModelAnimationAction
import gg.aquatic.aquaticcrates.plugin.animation.action.model.BMShowModelAction
import gg.aquatic.aquaticcrates.plugin.animation.action.model.HideModelAction
import gg.aquatic.aquaticcrates.plugin.animation.action.model.PlayModelAnimationAction
import gg.aquatic.aquaticcrates.plugin.animation.action.model.ShowModelAction
import gg.aquatic.aquaticcrates.plugin.animation.action.path.BindPathAction
import gg.aquatic.aquaticcrates.plugin.animation.action.path.LinearPathAction
import gg.aquatic.aquaticcrates.plugin.animation.action.path.SmoothPathAction
import gg.aquatic.aquaticcrates.plugin.animation.action.potion.ClearPotionEffectsAction
import gg.aquatic.aquaticcrates.plugin.animation.action.potion.PotionEffectsAction
import gg.aquatic.aquaticcrates.plugin.animation.action.timer.LaterActionsAction
import gg.aquatic.aquaticcrates.plugin.animation.action.timer.StartTickerAction
import gg.aquatic.aquaticcrates.plugin.animation.action.timer.TimedActionsAction
import gg.aquatic.aquaticcrates.plugin.animation.condition.CustomCondition
import gg.aquatic.aquaticcrates.plugin.animation.prop.inventory.AnimationMenu
import gg.aquatic.aquaticcrates.plugin.awaiters.AbstractAwaiter
import gg.aquatic.aquaticcrates.plugin.awaiters.IAAwaiter
import gg.aquatic.aquaticcrates.plugin.awaiters.MEGAwaiter
import gg.aquatic.aquaticcrates.plugin.awaiters.NexoAwaiter
import gg.aquatic.aquaticcrates.plugin.command.*
import gg.aquatic.aquaticcrates.plugin.condition.impl.CustomPlayerCondition
import gg.aquatic.aquaticcrates.plugin.condition.impl.DayRepeatCondition
import gg.aquatic.aquaticcrates.plugin.condition.impl.PermissionCondition
import gg.aquatic.aquaticcrates.plugin.condition.impl.WeekRepeatCondition
import gg.aquatic.aquaticcrates.plugin.interact.action.*
import gg.aquatic.aquaticcrates.plugin.log.LogMenuSettings
import gg.aquatic.aquaticcrates.plugin.misc.Messages
import gg.aquatic.aquaticcrates.plugin.misc.hook.BStatsHook
import gg.aquatic.aquaticcrates.plugin.misc.hook.CometHook
import gg.aquatic.aquaticcrates.plugin.misc.hook.PAPIHook
import gg.aquatic.aquaticcrates.plugin.preview.CratePreviewMenu
import gg.aquatic.aquaticcrates.plugin.reroll.input.interaction.InteractionInputHandler
import gg.aquatic.aquaticcrates.plugin.reroll.input.inventory.InventoryRerollInput
import gg.aquatic.aquaticcrates.plugin.reroll.input.inventory.RerollMenu
import gg.aquatic.aquaticcrates.plugin.restriction.impl.*
import gg.aquatic.aquaticcrates.plugin.reward.menu.RewardsMenuSettings
import gg.aquatic.aquaticcrates.plugin.serialize.CrateSerializer
import gg.aquatic.waves.api.event.event
import gg.aquatic.waves.api.event.packet.PacketContainerContentEvent
import gg.aquatic.waves.api.event.packet.PacketContainerSetSlotEvent
import gg.aquatic.waves.api.event.packet.PacketInteractEvent
import gg.aquatic.waves.command.AquaticBaseCommand
import gg.aquatic.waves.command.register
import gg.aquatic.waves.inventory.InventoryManager
import gg.aquatic.waves.inventory.event.AsyncPacketInventoryCloseEvent
import gg.aquatic.waves.item.ItemHandler
import gg.aquatic.waves.profile.ProfilesModule
import gg.aquatic.waves.registry.WavesRegistry
import gg.aquatic.waves.registry.registerAction
import gg.aquatic.waves.registry.registerRequirement
import gg.aquatic.waves.util.Config
import gg.aquatic.waves.util.action.ActionAnnotationProcessor
import gg.aquatic.waves.util.runAsyncTimer
import gg.aquatic.waves.util.toMMComponent
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.inventory.InventoryInteractEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.event.world.WorldLoadEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.persistence.PersistentDataType
import java.util.*
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

    override lateinit var settings: PluginSettings

    override fun onLoad() {
        AbstractCratesPlugin.INSTANCE = this
        spawnedCratesConfig = Config("spawnedcrates.yml", INSTANCE)
    }

    var loading = true
        private set

    lateinit var rewardsMenuSettings: RewardsMenuSettings
        private set

    lateinit var logMenuSettings: LogMenuSettings
        private set

    override fun onEnable() {
        registerObjects()

        if (server.pluginManager.getPlugin("Comet") != null) {
            CometHook()
        }
        BStatsHook.register()

        injectLegacyConverterListeners()

        ProfilesModule.registerModule(CrateProfileModule)
        PAPIHook.registerPAPIHook()

        val awaiters = mutableListOf<AbstractAwaiter>()
        if (Bukkit.getPluginManager().getPlugin("Nexo") != null) {
            val awaiter = NexoAwaiter()
            awaiters += awaiter
            awaiter.future.thenRun {
                awaiters -= awaiter
                if (awaiters.isEmpty()) {
                    load()
                }
            }
        }
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

        HistoryHandler.rewardHistory.clear()
        HistoryHandler.openHistory.clear()

        val (openHistory, rewardHistory) = CrateProfileDriver.loadGlobalHistory()
        HistoryHandler.openHistory += openHistory
        HistoryHandler.rewardHistory += rewardHistory

        if (awaiters.isEmpty()) {
            load()
        }

        event<WorldLoadEvent> {
            CrateHandler.onWorldLoad(it.world)
        }

        event<PlayerQuitEvent> {
            for (crate in CrateHandler.crates.values) {
                if (crate is OpenableCrate) {
                    crate.animationManager.forceStopAllAnimationTypes(it.player)
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
                    if (InteractionInputHandler.onSneak(it)) return@event
                    if (animationhandler.animationSettings.skippable) {
                        animationhandler.skipAnimation(it.player)
                    }
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
                "reload" to ReloadCommand,
                "rewardmenu" to RewardMenuCommand,
                "log" to LogCommand,
                "convert" to ConvertCommand
            ),
            Messages.HELP.message
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

        event<EntityDamageEvent>(ignoredCancelled = true) {
            val player = it.entity as? Player ?: return@event

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
                it.isCancelled = true
                return@event
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

        event<PlayerInteractEvent> {
            val player = it.player
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
                it.isCancelled = true
                InteractionInputHandler.onInteract(PacketInteractEvent(
                    it.player,
                    (it.action == Action.LEFT_CLICK_AIR || it.action == Action.LEFT_CLICK_BLOCK),
                    (it.hand == EquipmentSlot.OFF_HAND),
                    0,
                    when (it.action) {
                        Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK -> PacketInteractEvent.InteractType.ATTACK
                        Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK -> PacketInteractEvent.InteractType.INTERACT
                        else -> PacketInteractEvent.InteractType.INTERACT
                    }
                ))
            }
        }

        event<PacketInteractEvent> {
            val player = it.player
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
                it.isCancelled = true
                InteractionInputHandler.onInteract(it)
            }
        }
        event<PacketContainerSetSlotEvent> {
            val player = it.player
            if (it.inventoryId != 0) return@event

            var animation: CrateAnimation? = null

            for (value in CrateHandler.crates.values) {
                if (value !is OpenableCrate) {
                    continue
                }
                val animations = value.animationManager.playingAnimations[player.uniqueId] ?: continue
                for (animation1 in animations) {
                    if (animation1.state == CrateAnimation.State.FINISHED) continue
                    if (animation1.playerEquipment.isNotEmpty()) {
                        animation = animation1
                        break
                    }
                }
            }

            animation ?: return@event

            if (it.slot !in animation.playerEquipment.map { it.key.toSlot(player) }) return@event
            it.isCancelled = true
        }
        event<PacketContainerContentEvent> {
            val player = it.player
            if (it.inventoryId != 0) return@event

            var animation: CrateAnimation? = null

            for (value in CrateHandler.crates.values) {
                if (value !is OpenableCrate) {
                    continue
                }
                val animations = value.animationManager.playingAnimations[player.uniqueId] ?: continue
                for (animation1 in animations) {
                    if (animation1.state == CrateAnimation.State.FINISHED) continue
                    if (animation1.playerEquipment.isNotEmpty()) {
                        animation = animation1
                        break
                    }
                }
            }

            animation ?: return@event
            if (animation.state == CrateAnimation.State.FINISHED) return@event

            animation.playerEquipment.forEach { (slot, equipment) ->
                val intSlot = slot.toSlot(player)
                it.contents[intSlot] = equipment
            }
        }
    }

    fun reloadPlugin(): CompletableFuture<Boolean> {
        if (loading) {
            return CompletableFuture.completedFuture(false)
        }
        for (value in CrateHandler.crates.values) {
            if (value is OpenableCrate) {
                value.animationManager.forceStopAllAnimations()
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
        dataFolder.mkdirs()
        settings = CrateSerializer.loadPluginSettings()
        rewardsMenuSettings = CrateSerializer.loadRewardMenuSettings()
        logMenuSettings = CrateSerializer.loadLogMenuSettings()
        return runAsync {
            try {
                Messages.load()
                gg.aquatic.waves.util.message.Messages.injectMessages<Messages>("aquaticcrates")
                CrateHandler.crates += CrateSerializer.loadCrates()
                CrateHandler.loadSpawnedCrates(spawnedCratesConfig)

                for (crate in CrateHandler.crates.values) {
                    if (crate !is OpenableCrate) continue
                    val list =
                        HistoryHandler.latestRewards.getOrPut(crate.identifier) { Collections.synchronizedList(ArrayList()) }
                    val logs =
                        HistoryHandler.loadLogEntries(0, 10, null, crate.identifier, CrateProfileDriver.Sorting.NEWEST)
                    for ((playerName, history) in logs) {
                        for ((id, amt) in history.rewardIds) {
                            val crateReward = crate.rewardManager.rewards[id] ?: continue
                            val latestReward =
                                HistoryHandler.LatestReward(crateReward, history.timestamp, amt, playerName)
                            list.add(latestReward)
                            if (list.size >= 10) break
                        }
                        if (list.size >= 10) break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            loading = false
        }.exceptionally {
            it.printStackTrace()
            loading = false
            null
        }
    }

    private fun registerObjects() {
        // Animation Actions
        //WavesRegistry.registerAction("show-bossbar", ShowBossbarAction())
        //WavesRegistry.registerAction("hide-bossbar", HideBossbarAction())
        //WavesRegistry.registerAction("set-bossbar-message", SetBossbarTextAction())
        //WavesRegistry.registerAction("set-bossbar-color", SetBossbarColorAction())
        //WavesRegistry.registerAction("set-bossbar-style", SetBossbarStyleAction())
        //WavesRegistry.registerAction("set-bossbar-progress", SetBossbarProgressAction())
        //WavesRegistry.registerAction("linear-path", LinearPathAction())
        //WavesRegistry.registerAction("smooth-path", SmoothPathAction())
        //WavesRegistry.registerAction("show-entity", ShowEntityAction())
        //WavesRegistry.registerAction("hide-entity", HideEntityAction())
        //WavesRegistry.registerAction("throw-entity", ThrowEntityAction())
        //WavesRegistry.registerAction("update-entity-properties", UpdateEntityPropertiesAction())
        //WavesRegistry.registerAction("set-block", SetBlockAction())
        //WavesRegistry.registerAction("set-multiblock", SetMultiblockAction())
        //WavesRegistry.registerAction("show-model", ShowModelAction())
        //WavesRegistry.registerAction("play-model-animation", PlayModelAnimationAction())
        //WavesRegistry.registerAction("hide-model", HideModelAction())
        //WavesRegistry.registerAction("show-bm-model", BMShowModelAction())
        //WavesRegistry.registerAction("play-bm-model-animation", BMPlayModelAnimationAction())
        //WavesRegistry.registerAction("hide-bm-model", BMHideModelAction())
            //WavesRegistry.registerAction("play-sound", SoundAction())
            //WavesRegistry.registerAction("stop-sound", StopSoundAction())
        //WavesRegistry.registerAction("conditional-actions", ConditionalActionsAction())
        //WavesRegistry.registerAction("start-ticker", StartTickerAction())
        //WavesRegistry.registerAction("bind-path", BindPathAction())
            //WavesRegistry.registerAction("title", TitleAction())
        //WavesRegistry.registerAction("string-deobfuscation", StringDeobfuscationAction())
        //WavesRegistry.registerAction("push-player", PushPlayerAction())
        //WavesRegistry.registerAction("open-inventory", OpenInventoryAction())
        //WavesRegistry.registerAction("set-inventory-items", SetInventoryItemsAction())
        //WavesRegistry.registerAction("set-inventory-title", SetInventoryTitleAction())
        //WavesRegistry.registerAction("close-inventory", CloseInventoryAction())
        //WavesRegistry.registerAction("add-potion-effects", PotionEffectsAction())
        //WavesRegistry.registerAction("remove-potion-effects", ClearPotionEffectsAction())
        //WavesRegistry.registerAction("timed-actions", TimedActionsAction())
        //WavesRegistry.registerAction("delayed-actions", LaterActionsAction())
        //WavesRegistry.registerAction("rumbling-reward", RumblingRewardAction())
        //WavesRegistry.registerAction("player-equipment", EquipmentAnimationAction())
                //WavesRegistry.registerAction("player-actions", PlayerActionsAction())
        //WavesRegistry.registerAction("add-passenger", AddPassengerAction())
        //WavesRegistry.registerAction("remove-passenger", RemovePassengerAction())
        //WavesRegistry.registerAction("particle", ParticleAnimationAction())

        // Interaction Actions
        /*
        WavesRegistry.registerAction("open-crate", CrateOpenAction())
        WavesRegistry.registerAction("open-crate-instant", CrateInstantOpenAction())
        WavesRegistry.registerAction("preview-crate", CratePreviewAction())
        WavesRegistry.registerAction("destroy-crate", CrateBreakAction())
        WavesRegistry.registerAction("execute-actions", CrateExecuteActionsAction())
         */
        ActionAnnotationProcessor.process(this,"gg.aquatic.aquaticcrates.plugin.animation.action")
        ActionAnnotationProcessor.process(this,"gg.aquatic.aquaticcrates.plugin.interact.action")

        // Open Restrictions
        WavesRegistry.registerRequirement("player", PlayerOpenRestriction())
        WavesRegistry.registerRequirement("global", GlobalOpenRestriction())
        WavesRegistry.registerRequirement("all-player", AllPlayerOpenRestriction())
        WavesRegistry.registerRequirement("all-global", AllGlobalOpenRestriction())
        WavesRegistry.registerRequirement("world-blacklist", WorldBlacklistOpenRestriction())
        WavesRegistry.registerRequirement("full-inventory", EmptyInventoryOpenRestriction())

        // Player Conditions
        WavesRegistry.registerRequirement("custom", CustomPlayerCondition())
        WavesRegistry.registerRequirement("permission", PermissionCondition())
        WavesRegistry.registerRequirement("week-repeat", WeekRepeatCondition())
        WavesRegistry.registerRequirement("day-repeat", DayRepeatCondition())

        // Animation Action Conditions
        WavesRegistry.registerRequirement("custom", CustomCondition())

        // Prices
        PRICES += "crate-key" to CrateKeyPrice()
    }

    private fun injectLegacyConverterListeners() {

        event<AsyncPlayerPreLoginEvent>(ignoredCancelled = true) {
            if (loading) {
                it.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Component.text("Server is currently loading..."))
                return@event
            }
        }

        event<PlayerJoinEvent> {
            val player = it.player
            val inventory = player.inventory
            val contents = inventory.contents
            for (item in contents) {
                if (item == null) continue
                val meta = item.itemMeta ?: continue
                val pdc = meta.persistentDataContainer
                val oldNamespace = NamespacedKey("aquaticcrates", "keyidentifier")
                if (pdc.has(oldNamespace, PersistentDataType.STRING)) {
                    val keyId = pdc.get(oldNamespace, PersistentDataType.STRING)!!
                    pdc.remove(oldNamespace)
                    pdc.set(ItemHandler.NAMESPACE_KEY, PersistentDataType.STRING, "aquaticcrates-key:$keyId")
                    item.itemMeta = meta
                }
            }
        }

        event<InventoryOpenEvent>(ignoredCancelled = true) {
            val inventory = it.inventory
            val contents = inventory.contents
            for (item in contents) {
                if (item == null) continue
                val meta = item.itemMeta ?: continue
                val pdc = meta.persistentDataContainer
                val oldNamespace = NamespacedKey("aquaticcrates", "keyidentifier")
                if (pdc.has(oldNamespace, PersistentDataType.STRING)) {
                    val keyId = pdc.get(oldNamespace, PersistentDataType.STRING)!!
                    pdc.remove(oldNamespace)
                    pdc.set(ItemHandler.NAMESPACE_KEY, PersistentDataType.STRING, "aquaticcrates-key:$keyId")
                    item.itemMeta = meta
                }
            }
        }
    }
}