package gg.aquatic.aquaticcrates.plugin.serialize

import com.willfp.ecoitems.EcoItemsPlugin
import com.willfp.ecoitems.items.EcoItems
import gg.aquatic.aquaticcrates.api.PluginSettings
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.interaction.CrateInteractAction
import gg.aquatic.aquaticcrates.api.milestone.Milestone
import gg.aquatic.aquaticcrates.api.openprice.OpenPrice
import gg.aquatic.aquaticcrates.api.openprice.OpenPriceGroup
import gg.aquatic.aquaticcrates.api.openprice.impl.CrateKeyPrice
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.api.reward.RewardRarity
import gg.aquatic.aquaticcrates.api.reward.showcase.RewardShowcaseSerializer
import gg.aquatic.aquaticcrates.plugin.CratesPlugin
import gg.aquatic.aquaticcrates.plugin.animation.fail.FailAnimationSettings
import gg.aquatic.aquaticcrates.plugin.animation.idle.IdleAnimationSettings
import gg.aquatic.aquaticcrates.plugin.animation.open.AnimationManagerImpl
import gg.aquatic.aquaticcrates.plugin.animation.open.settings.CinematicAnimationSettings
import gg.aquatic.aquaticcrates.plugin.animation.open.settings.InstantAnimationSettings
import gg.aquatic.aquaticcrates.plugin.animation.open.settings.RegularAnimationSettings
import gg.aquatic.aquaticcrates.plugin.crate.BasicCrate
import gg.aquatic.aquaticcrates.plugin.crate.KeyImpl
import gg.aquatic.aquaticcrates.plugin.hologram.HologramSerializer
import gg.aquatic.aquaticcrates.plugin.interact.BasicCrateInteractHandler
import gg.aquatic.aquaticcrates.plugin.interact.KeyInteractHandlerImpl
import gg.aquatic.aquaticcrates.plugin.interact.action.CrateBreakAction
import gg.aquatic.aquaticcrates.plugin.interact.action.CrateInstantOpenAction
import gg.aquatic.aquaticcrates.plugin.interact.action.CrateOpenAction
import gg.aquatic.aquaticcrates.plugin.interact.action.CratePreviewAction
import gg.aquatic.aquaticcrates.plugin.log.LogMenuSettings
import gg.aquatic.aquaticcrates.plugin.milestone.MilestoneManagerImpl
import gg.aquatic.aquaticcrates.plugin.preview.CratePreviewMenuSettings
import gg.aquatic.aquaticcrates.plugin.reroll.RerollManagerImpl
import gg.aquatic.aquaticcrates.plugin.reroll.input.interaction.InteractionRerollInput
import gg.aquatic.aquaticcrates.plugin.reroll.input.inventory.InventoryRerollInput
import gg.aquatic.aquaticcrates.plugin.restriction.OpenData
import gg.aquatic.aquaticcrates.plugin.restriction.OpenRestrictionHandle
import gg.aquatic.aquaticcrates.plugin.restriction.impl.CrateRewardsRestriction
import gg.aquatic.aquaticcrates.plugin.reward.RewardManagerImpl
import gg.aquatic.aquaticcrates.plugin.reward.menu.RewardsMenuSettings
import gg.aquatic.waves.interactable.settings.BlockInteractableSettings
import gg.aquatic.waves.inventory.InventoryType
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.item.AquaticItemInteractEvent
import gg.aquatic.waves.menu.MenuSerializer
import gg.aquatic.waves.menu.settings.PrivateMenuSettings
import gg.aquatic.waves.registry.serializer.ActionSerializer
import gg.aquatic.waves.registry.serializer.InteractableSerializer
import gg.aquatic.waves.registry.serializer.RequirementSerializer
import gg.aquatic.waves.scenario.PlayerScenario
import gg.aquatic.waves.scenario.Scenario
import gg.aquatic.waves.util.Config
import gg.aquatic.waves.util.action.impl.MessageAction
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.block.impl.VanillaBlock
import gg.aquatic.waves.util.deepFilesLookup
import gg.aquatic.waves.util.generic.ClassTransform
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import gg.aquatic.waves.util.getSectionList
import gg.aquatic.waves.util.message.impl.SimpleMessage
import gg.aquatic.waves.util.requirement.ConfiguredRequirement
import gg.aquatic.waves.util.task.AsyncScope
import gg.aquatic.waves.util.toMMComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.io.File
import java.util.*
import kotlin.math.min

object CrateSerializer : BaseSerializer() {

    val animationSerializers = hashMapOf(
        "instant" to InstantAnimationSettings.Companion,
        "regular" to RegularAnimationSettings.Companion,
        "cinematic" to CinematicAnimationSettings.Companion,
    )
    val rerollInputSerializers = hashMapOf(
        "inventory" to InventoryRerollInput.Companion,
        "interaction" to InteractionRerollInput.Companion
    )

    fun loadPluginSettings(): PluginSettings {
        val config = Config("config.yml", CratesPlugin.getInstance())
        config.load()
        val cfg = config.getConfiguration()!!

        val useRewardsMenu = cfg.getBoolean("use-rewards-menu", true)
        val logOpenings = cfg.getBoolean("log-openings", true)
        val rewardThreshold = cfg.getInt("reward-threshold", 50)

        return PluginSettings(useRewardsMenu, logOpenings, rewardThreshold)
    }

    fun loadLogMenuSettings(): LogMenuSettings {
        val config = Config("config.yml", CratesPlugin.getInstance())
        config.load()
        val cfg = config.getConfiguration()!!

        val logSection = cfg.getConfigurationSection("log-menu") ?: return LogMenuSettings(
            PrivateMenuSettings(
                InventoryType.GENERIC9X1,
                "Example Title".toMMComponent(),
                hashMapOf()
            ),
            listOf()
        )
        val menu = MenuSerializer.loadPrivateInventory(logSection)
        return LogMenuSettings(
            menu,
            MenuSerializer.loadSlotSelection(logSection.getStringList("log-slots")).slots
        )
    }

    fun loadRewardMenuSettings(): RewardsMenuSettings {
        val config = Config("config.yml", CratesPlugin.getInstance())
        config.load()
        val cfg = config.getConfiguration()!!

        val rewardMenuSection = cfg.getConfigurationSection("reward-menu")
            ?: return RewardsMenuSettings(
                PrivateMenuSettings(
                    InventoryType.GENERIC9X1,
                    "Example Title".toMMComponent(),
                    hashMapOf()
                ),
                listOf(),
                listOf()
            )
        val menu = MenuSerializer.loadPrivateInventory(rewardMenuSection)
        val rewardSlots = MenuSerializer.loadSlotSelection(rewardMenuSection.getStringList("reward-slots"))
        val additionalLore = rewardMenuSection.getStringList("append-lore")
        return RewardsMenuSettings(
            menu,
            rewardSlots.slots,
            additionalLore
        )
    }

    suspend fun loadCrates(): HashMap<String, Crate> {
        //async()
        CratesPlugin.getInstance().dataFolder.mkdirs()
        val crates = HashMap<String, Crate>()

        val basicFolder = File(CratesPlugin.getInstance().dataFolder, "crates")
        basicFolder.mkdirs()

        val files = basicFolder.deepFilesLookup { it.extension == "yml" }
        val list = parallelForEach(files, min(files.size,idealThreads()), AsyncScope) {
            val crates = HashMap<String, Crate>()
            for (file in it) {
                try {
                    val crate = loadBasicCrate(file) ?: continue
                    crates += crate.identifier to crate
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            crates
        }.flatMap { it.values }
        list.forEach { crates[it.identifier] = it }
        return crates
    }

    private fun idealThreads(): Int {
        val cores = Runtime.getRuntime().availableProcessors()
        val maxThreads = cores * 2
        return maxThreads
    }

    private suspend fun <T,G> parallelForEach(
        values: Collection<T>,
        parallelism: Int,
        scope: CoroutineScope,
        block: suspend (list: List<T>) -> G
    ) = coroutineScope {
        val chunkSize = values.size/parallelism

        values.chunked(chunkSize).map { chunk ->
            scope.async {
                block(chunk)
            }
        }.awaitAll()
    }

    fun loadBasicCrate(file: File): BasicCrate? {
        val identifier = file.nameWithoutExtension
        val config = Config(file, CratesPlugin.getInstance())
        config.load()
        val cfg = config.getConfiguration()!!

        CratesPlugin.getInstance().logger.info("\nLoading crate $identifier\n")

        val interactableSections = cfg.getSectionList("interactables")
        val interactableSettings = interactableSections.mapNotNull { InteractableSerializer.load(it) }.toMutableList()
        if (interactableSettings.isEmpty()) {
            interactableSettings += BlockInteractableSettings(VanillaBlock(Material.STONE.createBlockData()), Vector())
        }

        val rerollInputSection = cfg.getConfigurationSection("reroll")
        val type = rerollInputSection?.getString("type") ?: "inventory"
        val serializer = rerollInputSerializers[type.lowercase()] ?: InventoryRerollInput.Companion
        val rerollManager = { crate: OpenableCrate ->
            val input = serializer.serialize(cfg)
            if (input != null) {
                val groups = hashMapOf<String, Int>()
                Bukkit.getConsoleSender().sendMessage("Loading reroll groups:")
                for (group in rerollInputSection?.getConfigurationSection("groups")?.getKeys(false) ?: emptyList()) {
                    val amount = cfg.getInt("reroll.groups.$group")
                    if (amount <= 0) continue
                    Bukkit.getConsoleSender().sendMessage("Loaded $group with $amount")
                    groups[group] = amount
                }

                val actions = ActionSerializer.fromSections<CrateAnimation>(
                    cfg.getSectionList("animation.reroll-tasks"), ClassTransform(
                        Player::class.java, { a -> a.player })
                ).toMutableList()

                RerollManagerImpl(crate, groups, input, actions)
            } else null
        }
        val keySection = cfg.getConfigurationSection("key")
        if (keySection == null) {
            sendConsoleMessage("Could not load Key! (Path: key)")
            return null
        }
        val keyItem = AquaticItem.loadFromYml(keySection)
        if (keyItem == null) {
            sendConsoleMessage("Could not load Key Item! (Path: key)")
            return null
        }

        val keyInteractActions = loadInteractActions(keySection.getConfigurationSection("interaction"))
        val key = { crate: OpenableCrate ->
            KeyImpl(
                crate,
                keyItem,
                cfg.getBoolean("key.must-be-held", false)
            ) { key ->
                KeyInteractHandlerImpl(
                    key,
                    keyInteractActions
                )
            }
        }

        val rewardSection = cfg.getConfigurationSection("rewards")
        if (rewardSection == null) {
            sendConsoleMessage("Could not load Rewards! (Path: rewards)")
            return null
        }

        val rarities = HashMap<String, RewardRarity>().apply {
            this += "default" to RewardRarity("default", "Default", 100.0)
        }
        cfg.getConfigurationSection("rarities")?.let { section ->
            for (key in section.getKeys(false)) {
                val raritySection = section.getConfigurationSection(key) ?: continue
                val displayName = raritySection.getString("display-name") ?: key
                val chance = raritySection.getDouble("chance", 1.0)
                rarities[key] = RewardRarity(key, displayName, chance)
            }
        }


        val milestones = TreeMap<Int, Milestone>()
        val repeatableMilestone = TreeMap<Int, Milestone>()
        val milestonesSection = cfg.getConfigurationSection("milestones")
        if (milestonesSection != null) {
            for (milestoneKey in milestonesSection.getKeys(false)) {
                val milestoneSection = milestonesSection.getConfigurationSection(milestoneKey) ?: break
                val name = milestoneSection.getString("display-name") ?: break
                val milestone = milestoneKey.toIntOrNull() ?: break

                val rewards =
                    loadRewards(milestoneSection.getConfigurationSection("rewards") ?: continue, rarities)
                Bukkit.getConsoleSender().sendMessage("Loaded milestone $milestone with ${rewards.size} rewards")
                milestones += milestone to Milestone(milestone, name.toMMComponent(), rewards.values.toList())
            }
            milestonesSection.getKeys(false).forEach { milestoneKey ->
                val milestoneSection = milestonesSection.getConfigurationSection(milestoneKey) ?: return@forEach
                val name = milestoneSection.getString("display-name") ?: return@forEach
                val milestone = milestoneKey.toIntOrNull() ?: return@forEach

                val rewards =
                    loadRewards(milestoneSection.getConfigurationSection("rewards") ?: return@forEach, rarities)
                Bukkit.getConsoleSender().sendMessage("Loaded milestone $milestone with ${rewards.size} rewards")
                milestones += milestone to Milestone(milestone, name.toMMComponent(), rewards.values.toList())
            }
        }
        cfg.getConfigurationSection("repeatable-milestones")?.let { repeatableMilestonesSection ->
            repeatableMilestonesSection.getKeys(false).forEach { repeatableMilestoneKey ->
                val repeatableMilestoneSection =
                    repeatableMilestonesSection.getConfigurationSection(repeatableMilestoneKey) ?: return@forEach
                val name = repeatableMilestoneSection.getString("display-name") ?: return@forEach
                val milestone = repeatableMilestoneKey.toIntOrNull() ?: return@forEach
                val rewards = loadRewards(
                    repeatableMilestoneSection.getConfigurationSection("rewards") ?: return@forEach,
                    rarities
                )
                Bukkit.getConsoleSender()
                    .sendMessage("Loaded repeatable milestone $milestone with ${rewards.size} rewards")
                repeatableMilestone += milestone to Milestone(
                    milestone,
                    name.toMMComponent(),
                    rewards.values.toList()
                )
            }
        }

        val milestoneManager = { crate: OpenableCrate ->
            MilestoneManagerImpl(
                crate, milestones, repeatableMilestone
            )
        }

        val interactHandler = { crate: OpenableCrate ->
            val clickActions = loadInteractActions(cfg.getConfigurationSection("interaction"))
            if (clickActions.isEmpty()) {
                clickActions += AquaticItemInteractEvent.InteractType.LEFT to ConfiguredExecutableObject(
                    CratePreviewAction(),
                    ObjectArguments(mapOf())
                )
                clickActions += AquaticItemInteractEvent.InteractType.RIGHT to ConfiguredExecutableObject(
                    CrateOpenAction(),
                    ObjectArguments(mapOf())
                )
                clickActions += AquaticItemInteractEvent.InteractType.SHIFT_RIGHT to ConfiguredExecutableObject(
                    CrateInstantOpenAction(),
                    ObjectArguments(mapOf())
                )
                clickActions += AquaticItemInteractEvent.InteractType.SHIFT_LEFT to ConfiguredExecutableObject(
                    CrateBreakAction(),
                    ObjectArguments(mapOf())
                )
            }
            BasicCrateInteractHandler(crate, clickActions)
        }

        val previewMenuPages = ArrayList<CratePreviewMenuSettings>()
        val previewSection = cfg.getConfigurationSection("preview")
        if (previewSection != null) {
            if (!previewSection.contains("pages")) {
                val settings = loadCratePreviewMenuSettings(previewSection)
                previewMenuPages += settings
            } else {
                val sections = previewSection.getSectionList("pages")
                for (section in sections) {
                    val settings = loadCratePreviewMenuSettings(section)
                    previewMenuPages += settings
                }
            }
        }

        val animationSettingsFactory = cfg.getString("animation.type", "instant")!!.lowercase()
        val animationFactory = animationSerializers[animationSettingsFactory] ?: InstantAnimationSettings.Companion
        val animationSettings = animationFactory.serialize(cfg.getConfigurationSection("animation"))
            ?: InstantAnimationSettings.serialize(null)
        Bukkit.getConsoleSender().sendMessage("Loaded ${animationSettings.animationTasks.size} animation tasks")

        val rewards = loadRewards(rewardSection, rarities)
        val guaranteedRewardsSection = cfg.getConfigurationSection("guaranteed-rewards")
        val guaranteedRewards = TreeMap<Int, Reward>()
        if (guaranteedRewardsSection != null) {
            for (milestoneStr in guaranteedRewardsSection.getKeys(false)) {
                val milestone = milestoneStr.toIntOrNull() ?: continue
                val rewardId = guaranteedRewardsSection.getString(milestoneStr) ?: continue
                val reward = rewards[rewardId] ?: continue
                guaranteedRewards[milestone] = reward
                Bukkit.getConsoleSender().sendMessage("Loaded guaranteed reward $rewardId for milestone $milestone")
            }
        }

        val massOpenFinalActions =
            ActionSerializer.fromSections<Player>(cfg.getSectionList("mass-open.final-tasks")).toMutableList()
        val massOpenPerRewardActions =
            ActionSerializer.fromSections<Player>(cfg.getSectionList("mass-open.per-reward-tasks")).toMutableList()

        val emptyCrateMessage = cfg.getString("empty-crate-message")
        var foundEmptyRestriction = false
        val openRestrictions =
            cfg.getSectionList("open-restrictions").mapNotNull { section ->
                val type = section.getString("type") ?: return@mapNotNull null
                if (type.lowercase() == "available-rewards") {
                    foundEmptyRestriction = true
                }
                val restriction = RequirementSerializer.fromSection<OpenData>(
                    section, ClassTransform(
                        Player::class.java
                    ) { d -> d.player }) ?: return@mapNotNull null
                val failActions = ActionSerializer.fromSections<OpenData>(
                    section.getSectionList("fail-actions"), ClassTransform(
                        Player::class.java
                    ) { d -> d.player })

                OpenRestrictionHandle(restriction, failActions)
            }.toMutableList()

        if (!foundEmptyRestriction && emptyCrateMessage != null) {
            openRestrictions += OpenRestrictionHandle(
                ConfiguredRequirement(
                    CrateRewardsRestriction(),
                    ObjectArguments(mapOf("available-rewards" to 1))
                ),
                listOf(
                    ConfiguredExecutableObject(
                        ActionSerializer.TransformedAction(MessageAction()) { d -> d.player },
                        ObjectArguments(mapOf("message" to SimpleMessage(emptyCrateMessage)))
                    )
                )
            )
        }

        val openPriceGroups = ArrayList<OpenPriceGroup>()
        for (groupSection in cfg.getSectionList("open-price-groups")) {
            val prices = ArrayList<OpenPrice>()
            for (priceSection in groupSection.getSectionList("prices")) {
                val price = gg.aquatic.aquaticcrates.api.openprice.PriceSerializer.fromSection(priceSection, identifier) ?: continue
                val failActions = ActionSerializer.fromSections<Player>(priceSection.getSectionList("fail-actions"))

                prices += OpenPrice(price, failActions.toMutableList())
            }
            val failActions = ActionSerializer.fromSections<Player>(groupSection.getSectionList("fail-actions"))
            val priceGroup = OpenPriceGroup(
                prices.toMutableList(),
                failActions.toMutableList()
            )
            openPriceGroups += priceGroup
        }
        val noKeyMessage = cfg.getString("no-key-message")
        if (openPriceGroups.isEmpty()) openPriceGroups += OpenPriceGroup(
            mutableListOf(
                OpenPrice(
                    gg.aquatic.aquaticcrates.api.openprice.ConfiguredPrice(
                        ObjectArguments(
                            hashMapOf(
                                "crate" to identifier
                            )
                        ),
                        CrateKeyPrice()
                    ),
                    noKeyMessage?.let {
                        mutableListOf(
                            ConfiguredExecutableObject(
                                MessageAction(),
                                ObjectArguments(
                                    mapOf(
                                        "message" to SimpleMessage(noKeyMessage)
                                    )
                                )
                            )
                        )
                    } ?: mutableListOf()
                )
            ),
            mutableListOf()
        )
        Bukkit.getConsoleSender().sendMessage("Loaded ${openPriceGroups.sumOf { it.prices.size }} open price groups")

        val defaultRewardShowcase = cfg.getConfigurationSection("showcase")?.let {
            RewardShowcaseSerializer.load(it)
        }

        CratesPlugin.getInstance().logger.info("Loaded crate hologram!")
        val hologram = HologramSerializer.loadAquaticHologram(cfg.getConfigurationSection("hologram"))
        if (hologram == null) {
            CratesPlugin.getInstance().logger.warning("Failed to load crate hologram!");
        }

        return BasicCrate(
            identifier,
            cfg.getString("display-name") ?: identifier,
            hologram,
            interactableSettings,
            openPriceGroups,
            { bc ->
                AnimationManagerImpl(
                    bc,
                    animationSettings,
                    loadIdleAnimationSettings(cfg).apply {
                        Bukkit.getConsoleSender().sendMessage("Loaded $size idle animations")
                    },
                    loadFailAnimationSettings(cfg),
                    rerollManager,
                )
            },
            key,
            { bc ->
                val possibleRewardRanges = loadRewardRanges(cfg.getSectionList("possible-rewards"))
                RewardManagerImpl(bc, possibleRewardRanges, guaranteedRewards, milestoneManager, rewards)
            },
            interactHandler,
            previewMenuPages,
            massOpenFinalActions,
            //massOpenPerRewardActions,
            openRestrictions,
            defaultRewardShowcase,
            cfg.getBoolean("disable-open-logging",false),
        )
    }

    private fun loadFailAnimationSettings(cfg: FileConfiguration): FailAnimationSettings? {
        val section = cfg.getConfigurationSection("fail-animation") ?: return null
        val actionsSection = section.getConfigurationSection("actions") ?: return null
        val actions = TreeMap<Int, Collection<ConfiguredExecutableObject<PlayerScenario, Unit>>>()
        for (key in actionsSection.getKeys(false)) {
            val time = key.toIntOrNull() ?: continue
            val playerAnimationActions =
                ActionSerializer.fromSections<PlayerScenario>(
                    actionsSection.getSectionList(key),
                    ClassTransform(
                        Player::class.java
                    ) { a -> a.player })
            actions[time] = playerAnimationActions
        }
        val length = section.getInt("length", -1)
        if (length < 0) return null
        return FailAnimationSettings(actions, length)
    }

    private fun loadIdleAnimationSettings(cfg: FileConfiguration): List<IdleAnimationSettings> {
        val list = ArrayList<IdleAnimationSettings>()
        for (configurationSection in cfg.getSectionList("idle-animations")) {
            val actionsSection = configurationSection.getConfigurationSection("actions") ?: continue
            val actions = TreeMap<Int, MutableList<ConfiguredExecutableObject<Scenario, Unit>>>()
            for (key in actionsSection.getKeys(false)) {
                val time = key.toIntOrNull() ?: continue
                actions.getOrPut(time) { Collections.synchronizedList(ArrayList()) } += ActionSerializer.fromSections<Scenario>(
                    actionsSection.getSectionList(key)
                )
            }
            val isLoop = configurationSection.getBoolean("loop", false)
            val length = configurationSection.getInt("length", -1)
            val chance = configurationSection.getDouble("chance", 1.0)
            if (length < 0) continue

            list += IdleAnimationSettings(
                actions, length, isLoop, chance
            )
            Bukkit.getConsoleSender().sendMessage("Loaded idle animation with ${actions.size} tasks")
        }
        return list
    }

    private fun loadCratePreviewMenuSettings(section: ConfigurationSection): CratePreviewMenuSettings {
        val rewardSlots = MenuSerializer.loadSlotSelection(section.getStringList("reward-slots"))
        val invSettings = MenuSerializer.loadPrivateInventory(section)
        val clearBottomInventory = section.getBoolean("clear-bottom-inventory", false)

        val randomRewardsSlots = MenuSerializer.loadSlotSelection(section.getStringList("random-rewards.slots"))
        val changeDuration = section.getInt("random-rewards.change-duration")

        val additionalRewardLore = section.getStringList("reward-lore")
        val updateRewardItemsEvery = section.getInt("update-reward-items-every", 1)

        return CratePreviewMenuSettings(
            invSettings,
            clearBottomInventory,
            rewardSlots.slots,
            CratePreviewMenuSettings.RandomRewardsSettings(randomRewardsSlots.slots, changeDuration),
            additionalRewardLore,
            updateRewardItemsEvery
        )
    }

    fun loadInteractActions(section: ConfigurationSection?): EnumMap<AquaticItemInteractEvent.InteractType, ConfiguredExecutableObject<CrateInteractAction, Unit>> {
        val map = EnumMap<AquaticItemInteractEvent.InteractType, ConfiguredExecutableObject<CrateInteractAction, Unit>>(
            AquaticItemInteractEvent.InteractType::class.java
        )
        section ?: return map
        for (key in section.getKeys(false)) {
            val actionSection = section.getConfigurationSection(key) ?: continue
            val type = AquaticItemInteractEvent.InteractType.valueOf(key.uppercase())
            val action = ActionSerializer.fromSection<CrateInteractAction>(actionSection) ?: continue
            map[type] = action
        }
        return map
    }

}