package gg.aquatic.aquaticcrates.plugin.serialize

import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.interaction.CrateInteractAction
import gg.aquatic.aquaticcrates.api.openprice.OpenPriceGroup
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.plugin.CratesPlugin
import gg.aquatic.aquaticcrates.plugin.animation.crate.AnimationManagerImpl
import gg.aquatic.aquaticcrates.plugin.animation.crate.settings.InstantAnimationSettings
import gg.aquatic.aquaticcrates.plugin.animation.crate.settings.RegularAnimationSettings
import gg.aquatic.aquaticcrates.plugin.crate.BasicCrate
import gg.aquatic.aquaticcrates.plugin.crate.KeyImpl
import gg.aquatic.aquaticcrates.plugin.interact.KeyInteractHandlerImpl
import gg.aquatic.aquaticcrates.plugin.hologram.HologramSerializer
import gg.aquatic.aquaticcrates.plugin.interact.BasicCrateInteractHandler
import gg.aquatic.aquaticcrates.plugin.interact.action.CrateBreakAction
import gg.aquatic.aquaticcrates.plugin.interact.action.CrateInstantOpenAction
import gg.aquatic.aquaticcrates.plugin.interact.action.CrateOpenAction
import gg.aquatic.aquaticcrates.plugin.interact.action.CratePreviewAction
import gg.aquatic.aquaticcrates.plugin.milestone.MilestoneManagerImpl
import gg.aquatic.aquaticcrates.plugin.preview.CratePreviewMenuSettings
import gg.aquatic.aquaticcrates.plugin.reroll.RerollManagerImpl
import gg.aquatic.aquaticcrates.plugin.reroll.input.inventory.InventoryRerollInput
import gg.aquatic.aquaticcrates.plugin.reward.RewardManagerImpl
import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import gg.aquatic.aquaticseries.lib.block.impl.VanillaBlock
import gg.aquatic.aquaticseries.lib.util.Config
import gg.aquatic.aquaticseries.lib.util.getSectionList
import gg.aquatic.waves.interactable.settings.BlockInteractableSettings
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.item.AquaticItemInteractEvent
import gg.aquatic.waves.item.loadFromYml
import gg.aquatic.waves.registry.serializer.ActionSerializer
import gg.aquatic.waves.registry.serializer.InteractableSerializer
import gg.aquatic.waves.registry.serializer.InventorySerializer
import gg.aquatic.waves.registry.serializer.RequirementSerializer
import io.ktor.server.config.ConfigLoader.Companion.load
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object CrateSerializer : BaseSerializer() {

    val animationSerializers = hashMapOf(
        "instant" to InstantAnimationSettings.Companion,
        "regular" to RegularAnimationSettings.Companion,
    )
    val rerollInputSerializers = hashMapOf(
        "inventory" to InventoryRerollInput.Companion
    )

    fun loadCrates(): HashMap<String, Crate> {
        CratesPlugin.INSTANCE.dataFolder.mkdirs()
        val crates = HashMap<String, Crate>()

        val basicFolder = File(CratesPlugin.INSTANCE.dataFolder, "crates")
        basicFolder.mkdirs()

        crates += loadBasicCrates(basicFolder)

        return crates
    }

    fun loadBasicCrates(folder: File): HashMap<String, BasicCrate> {
        val crates = HashMap<String, BasicCrate>()
        for (file in folder.listFiles()!!) {
            if (file.isDirectory) {
                crates += loadBasicCrates(file)
                continue
            }
            val c = loadBasicCrate(file) ?: continue
            crates[c.identifier] = c
        }
        return crates
    }

    fun loadBasicCrate(file: File): BasicCrate? {
        val identifier = file.nameWithoutExtension
        val config = Config(file, CratesPlugin.INSTANCE)
        config.load()
        val cfg = config.getConfiguration()!!

        val interactableSections = cfg.getSectionList("interactables")
        val interactableSettings = interactableSections.mapNotNull { InteractableSerializer.load(it) }.toMutableList()
        if (interactableSettings.isEmpty()) {
            interactableSettings += BlockInteractableSettings(VanillaBlock(Material.STONE.createBlockData()), Vector())
        }

        val openRequirements =
            RequirementSerializer.fromSections<Player>(cfg.getSectionList("open-requirements")).toMutableList()
        val openPriceGroups = ArrayList<OpenPriceGroup>()

        val rerollInputSection = cfg.getConfigurationSection("reroll")
        val type = rerollInputSection?.getString("type", "inventory") ?: "inventory"
        val serializer = rerollInputSerializers[type] ?: InventoryRerollInput.Companion
        val rerollManager = { crate: OpenableCrate ->
            val input = serializer.serialize(cfg)
            if (input != null) {
                RerollManagerImpl(crate, hashMapOf(), input)
            }
            null
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
                    cfg.getBoolean("key.requires-crate-to-open"),
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

        val milestoneManager = { crate: OpenableCrate ->
            MilestoneManagerImpl(
                crate, TreeMap(), TreeMap()
            )
        }

        val interactHandler = { crate: OpenableCrate ->
            val clickActions = loadInteractActions(cfg.getConfigurationSection("interaction"))
            if (clickActions.isEmpty()) {
                clickActions += AquaticItemInteractEvent.InteractType.LEFT to ConfiguredAction(
                    CratePreviewAction(),
                    mapOf()
                )
                clickActions += AquaticItemInteractEvent.InteractType.RIGHT to ConfiguredAction(
                    CrateOpenAction(),
                    mapOf()
                )
                clickActions += AquaticItemInteractEvent.InteractType.SHIFT_RIGHT to ConfiguredAction(
                    CrateInstantOpenAction(),
                    mapOf()
                )
                clickActions += AquaticItemInteractEvent.InteractType.SHIFT_LEFT to ConfiguredAction(
                    CrateBreakAction(),
                    mapOf()
                )
            }
            BasicCrateInteractHandler(crate, clickActions)
        }

        val previewMenuPages = ArrayList<CratePreviewMenuSettings>()
        val previewSection = cfg.getConfigurationSection("preview")
        if (previewSection != null) {
            if (!previewSection.contains("pages")) {
                val settings = loadCratePreviewMenuSettings(previewSection)
                if (settings != null) {
                    previewMenuPages += settings
                }
            } else {
                val sections = previewSection.getSectionList("pages")
                for (section in sections) {
                    val settings = loadCratePreviewMenuSettings(section)
                    if (settings != null) {
                        previewMenuPages += settings
                    }
                }
            }
        }

        val animationSettingsFactory = cfg.getString("animation.type", "instant")!!.lowercase()
        val animationFactory = animationSerializers[animationSettingsFactory] ?: InstantAnimationSettings.Companion
        val animationSettings = animationFactory.serialize(cfg.getConfigurationSection("animation"))
            ?: InstantAnimationSettings.serialize(null)
        Bukkit.getConsoleSender().sendMessage("Loaded ${animationSettings.animationTasks.size} animation tasks")

        val guaranteedRewardsSection = cfg.getConfigurationSection("guaranteed-rewards")
        val guaranteedRewards = HashMap<Int, Reward>()
        if (guaranteedRewardsSection != null) {
            for (milestoneStr in guaranteedRewardsSection.getKeys(false)) {
                val milestone = milestoneStr.toIntOrNull() ?: continue
                val section = guaranteedRewardsSection.getConfigurationSection(milestoneStr) ?: continue
                val reward = loadReward(section) ?: continue
                guaranteedRewards += milestone to reward
            }
        }

        val massOpenFinalActions = ActionSerializer.fromSections<Player>(cfg.getSectionList("mass-open.final-tasks")).toMutableList()
        val massOpenPerRewardActions = ActionSerializer.fromSections<Player>(cfg.getSectionList("mass-open.per-reward-tasks")).toMutableList()

        return BasicCrate(
            identifier,
            cfg.getString("display-name") ?: identifier,
            HologramSerializer.loadAquaticHologram(cfg.getConfigurationSection("hologram")),
            interactableSettings,
            openRequirements,
            openPriceGroups,
            { bc ->
                AnimationManagerImpl(
                    bc,
                    animationSettings,
                    rerollManager
                )
            },
            key,
            { bc ->
                val possibleRewardRanges = loadRewardRanges(cfg.getSectionList("possible-rewards"))
                val rewards = loadRewards(rewardSection)
                RewardManagerImpl(bc, possibleRewardRanges, guaranteedRewards, milestoneManager, rewards)
            },
            interactHandler,
            previewMenuPages,
            massOpenFinalActions,
            massOpenPerRewardActions
        )
    }

    private fun loadCratePreviewMenuSettings(section: ConfigurationSection): CratePreviewMenuSettings? {
        val rewardSlots = section.getIntegerList("reward-slots")
        val invSettings = InventorySerializer.loadInventory(section) ?: return null
        val clearBottomInventory = section.getBoolean("clear-bottom-inventory", false)

        val randomRewardsSlots = section.getIntegerList("random-rewards.slots")
        val changeDuration = section.getInt("random-rewards.change-duration")

        return CratePreviewMenuSettings(
            invSettings,
            clearBottomInventory,
            rewardSlots,
            CratePreviewMenuSettings.RandomRewardsSettings(randomRewardsSlots, changeDuration)
        )
    }

    fun loadInteractActions(section: ConfigurationSection?): EnumMap<AquaticItemInteractEvent.InteractType, ConfiguredAction<CrateInteractAction>> {
        val map = EnumMap<AquaticItemInteractEvent.InteractType, ConfiguredAction<CrateInteractAction>>(
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