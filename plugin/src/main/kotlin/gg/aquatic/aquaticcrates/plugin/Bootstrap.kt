package gg.aquatic.aquaticcrates.plugin

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
import gg.aquatic.aquaticcrates.plugin.animation.condition.CustomCondition
import gg.aquatic.aquaticcrates.plugin.animation.prop.inventory.AnimationMenu
import gg.aquatic.aquaticcrates.plugin.awaiters.*
import gg.aquatic.aquaticcrates.plugin.command.*
import gg.aquatic.aquaticcrates.plugin.condition.impl.CustomPlayerCondition
import gg.aquatic.aquaticcrates.plugin.condition.impl.DayRepeatCondition
import gg.aquatic.aquaticcrates.plugin.condition.impl.PermissionCondition
import gg.aquatic.aquaticcrates.plugin.condition.impl.WeekRepeatCondition
import gg.aquatic.aquaticcrates.plugin.misc.Messages
import gg.aquatic.aquaticcrates.plugin.misc.hook.BStatsHook
import gg.aquatic.aquaticcrates.plugin.misc.hook.CometHook
import gg.aquatic.aquaticcrates.plugin.misc.hook.HMCCosmeticsHook
import gg.aquatic.aquaticcrates.plugin.misc.hook.PAPIHook
import gg.aquatic.aquaticcrates.plugin.preview.CratePreviewMenu
import gg.aquatic.aquaticcrates.plugin.reroll.input.interaction.InteractionInputHandler
import gg.aquatic.aquaticcrates.plugin.reroll.input.inventory.InventoryRerollInput
import gg.aquatic.aquaticcrates.plugin.reroll.input.inventory.RerollMenu
import gg.aquatic.aquaticcrates.plugin.restriction.impl.*
import gg.aquatic.aquaticcrates.plugin.serialize.CrateSerializer
import gg.aquatic.waves.api.event.event
import gg.aquatic.waves.api.event.packet.PacketContainerContentEvent
import gg.aquatic.waves.api.event.packet.PacketContainerSetSlotEvent
import gg.aquatic.waves.api.event.packet.PacketInteractEvent
import gg.aquatic.waves.command.AquaticBaseCommand
import gg.aquatic.waves.command.register
import gg.aquatic.waves.inventory.InventoryManager
import gg.aquatic.waves.inventory.event.AsyncPacketInventoryCloseEvent
import gg.aquatic.waves.profile.ProfilesModule
import gg.aquatic.waves.registry.WavesRegistry
import gg.aquatic.waves.registry.registerRequirement
import gg.aquatic.waves.util.Config
import gg.aquatic.waves.util.action.ActionAnnotationProcessor
import gg.aquatic.waves.util.requirement.RequirementAnnotationProcessor
import gg.aquatic.waves.util.runAsyncTimer
import gg.aquatic.waves.util.task.AsyncCtx
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.inventory.InventoryInteractEvent
import org.bukkit.event.player.*
import org.bukkit.event.world.WorldLoadEvent
import org.bukkit.inventory.EquipmentSlot
import java.io.File
import java.net.URLDecoder
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.jar.JarFile

object Bootstrap {

    lateinit var spawnedCratesConfig: Config

    internal fun onLoad() {
        spawnedCratesConfig = Config("spawnedcrates.yml", CratesPlugin.getInstance())
        loadExampleCrates()
    }

    internal fun enable() {
        registerObjects()

        if (Bukkit.getServer().pluginManager.getPlugin("Comet") != null) {
            CometHook()
        }
        if (Bukkit.getServer().pluginManager.getPlugin("HMCCosmetics") != null) {
            HMCCosmeticsHook()
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
        if (Bukkit.getPluginManager().getPlugin("CraftEngine") != null) {
            val awaiter = CEAwaiter()
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
        if (Bukkit.getPluginManager().getPlugin("EcoItems") != null) {
            val awaiter = EcoAwaiter()
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

        BaseCommand.setup().register()
        /*
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
            {
                Messages.HELP.message
            }).register("aquaticcrates")
         */

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
                InteractionInputHandler.onInteract(
                    PacketInteractEvent(
                        it.player,
                        (it.action == Action.LEFT_CLICK_AIR || it.action == Action.LEFT_CLICK_BLOCK),
                        (it.hand == EquipmentSlot.OFF_HAND),
                        0,
                        when (it.action) {
                            Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK -> PacketInteractEvent.InteractType.ATTACK
                            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK -> PacketInteractEvent.InteractType.INTERACT
                            else -> PacketInteractEvent.InteractType.INTERACT
                        }
                    )
                )
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
                    if (animation1.phase is CrateAnimation.FinalPhase) continue
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
                    if (animation1.phase is CrateAnimation.FinalPhase) continue
                    if (animation1.playerEquipment.isNotEmpty()) {
                        animation = animation1
                        break
                    }
                }
            }

            animation ?: return@event
            if (animation.phase is CrateAnimation.FinalPhase) return@event

            animation.playerEquipment.forEach { (slot, equipment) ->
                val intSlot = slot.toSlot(player)
                it.contents[intSlot] = equipment
            }
        }
    }

    private fun startTicker() {
        runAsyncTimer(1,1) {
            try {
                for ((_, crate) in CrateHandler.crates) {
                    if (crate is OpenableCrate) {
                        crate.animationManager.tick()
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    internal fun load(): CompletableFuture<Unit> {
        CratesPlugin.getInstance().isLoading = true
        CratesPlugin.getInstance().dataFolder.mkdirs()
        CratesPlugin.getInstance().setSettings(CrateSerializer.loadPluginSettings())
        CratesPlugin.getInstance().rewardsMenuSettings = CrateSerializer.loadRewardMenuSettings()
        CratesPlugin.getInstance().logMenuSettings = CrateSerializer.loadLogMenuSettings()

        return AsyncCtx.scope.async {
            try {
                Messages.load()
                gg.aquatic.waves.util.message.Messages.injectMessages<Messages>("aquaticcrates")
                loadExampleCrates()

                val time = System.currentTimeMillis()
                CrateHandler.crates += CrateSerializer.loadCrates()
                println("Loaded ${CrateHandler.crates.size} crates in ${System.currentTimeMillis() - time}ms")
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
                                HistoryHandler.LatestReward(crateReward, history.timeStamp, amt, playerName)
                            list.add(latestReward)
                            if (list.size >= 10) break
                        }
                        if (list.size >= 10) break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            CratesPlugin.getInstance().isLoading = false
        }.asCompletableFuture()
    }


    private fun registerObjects() {

        // Open Restrictions
        WavesRegistry.registerRequirement("player", PlayerOpenRestriction())
        WavesRegistry.registerRequirement("global", GlobalOpenRestriction())
        WavesRegistry.registerRequirement("all-player", AllPlayerOpenRestriction())
        WavesRegistry.registerRequirement("all-global", AllGlobalOpenRestriction())
        WavesRegistry.registerRequirement("world-blacklist", WorldBlacklistOpenRestriction())
        WavesRegistry.registerRequirement("full-inventory", EmptyInventoryOpenRestriction())
        WavesRegistry.registerRequirement("available-rewards", CrateRewardsRestriction())
        WavesRegistry.registerRequirement("per-player-open-limit", PlayerLimitOpenRestriction())
        WavesRegistry.registerRequirement("global-open-limit", GlobalLimitOpenRestriction())

        // Player Conditions
        WavesRegistry.registerRequirement("custom", CustomPlayerCondition())
        WavesRegistry.registerRequirement("permission", PermissionCondition())
        WavesRegistry.registerRequirement("week-repeat", WeekRepeatCondition())
        WavesRegistry.registerRequirement("day-repeat", DayRepeatCondition())

        // Animation Action Conditions
        WavesRegistry.registerRequirement("custom", CustomCondition())

        // Prices
        PRICES += "crate-key" to CrateKeyPrice()

        ActionAnnotationProcessor.process(this, "gg.aquatic.aquaticcrates.plugin")
        RequirementAnnotationProcessor.process(this, "gg.aquatic.aquaticcrates.plugin")
    }

    private fun injectLegacyConverterListeners() {

        event<AsyncPlayerPreLoginEvent>(ignoredCancelled = true) {
            if (CratesPlugin.getInstance().isLoading) {
                it.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    Component.text("Server is currently loading...")
                )
                return@event
            }
        }
    }

    fun getResourceFileNames(folderPath: String): List<String> {
        val fileNames = mutableListOf<String>()

        try {
            val jarPath = javaClass.protectionDomain.codeSource.location.path
            val jarFile = JarFile(URLDecoder.decode(jarPath, "UTF-8"))

            val entries = jarFile.entries()
            val folderPrefix = if (folderPath.endsWith("/")) folderPath else "$folderPath/"

            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                val name = entry.name

                if (name.startsWith(folderPrefix) && !entry.isDirectory) {
                    val fileName = name.substring(folderPrefix.length)
                    if (!fileName.contains("/")) { // Ensure it's not in a subdirectory
                        fileNames.add(fileName)
                    }
                }
            }

            jarFile.close()
        } catch (e: Exception) {
            CratesPlugin.getInstance().logger.warning("Failed to read resource files: ${e.message}")
        }

        return fileNames
    }

    private fun loadExampleCrates() {
        val cratesFolder = CratesPlugin.getInstance().dataFolder.resolve("crates")
        if (!cratesFolder.exists() || cratesFolder.listFiles().isEmpty()) {
            cratesFolder.mkdirs()

            // Get all file names in the examples resource folder
            val exampleFileNames = getResourceFileNames("examplecrates")

            // Copy each example file to the data folder
            for (fileName in exampleFileNames) {
                val resourcePath = "examplecrates/$fileName"

                // Use getResource from JavaPlugin to get the file
                CratesPlugin.getInstance().getResource(resourcePath)?.use { inputStream ->
                    val targetFile = File(cratesFolder, fileName)
                    targetFile.outputStream().use { output ->
                        inputStream.copyTo(output)
                        CratesPlugin.getInstance().logger.info("Copied example file: $fileName")
                    }
                }
            }
        }
    }
}