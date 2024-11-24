package gg.aquatic.aquaticcrates.plugin.serialize

import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.interaction.CrateInteractAction
import gg.aquatic.aquaticcrates.api.openprice.OpenPriceGroup
import gg.aquatic.aquaticcrates.plugin.CratesPlugin
import gg.aquatic.aquaticcrates.plugin.animation.crate.CrateAnimationManagerImpl
import gg.aquatic.aquaticcrates.plugin.animation.crate.settings.InstantAnimationSettings
import gg.aquatic.aquaticcrates.plugin.crate.BasicCrate
import gg.aquatic.aquaticcrates.plugin.crate.KeyImpl
import gg.aquatic.aquaticcrates.plugin.interact.KeyInteractHandlerImpl
import gg.aquatic.aquaticcrates.plugin.hologram.HologramSerializer
import gg.aquatic.aquaticcrates.plugin.interact.BasicCrateInteractHandler
import gg.aquatic.aquaticcrates.plugin.interact.action.CrateInstantOpenAction
import gg.aquatic.aquaticcrates.plugin.interact.action.CrateOpenAction
import gg.aquatic.aquaticcrates.plugin.interact.action.CratePreviewAction
import gg.aquatic.aquaticcrates.plugin.milestone.MilestoneManagerImpl
import gg.aquatic.aquaticcrates.plugin.reroll.RerollManagerImpl
import gg.aquatic.aquaticcrates.plugin.reward.RewardManagerImpl
import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import gg.aquatic.aquaticseries.lib.util.Config
import gg.aquatic.aquaticseries.lib.util.getSectionList
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.item.AquaticItemInteractEvent
import gg.aquatic.waves.item.loadFromYml
import gg.aquatic.waves.registry.serializer.ActionSerializer
import gg.aquatic.waves.registry.serializer.InteractableSerializer
import gg.aquatic.waves.registry.serializer.RequirementSerializer
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object CrateSerializer : BaseSerializer() {

    val animationSerializers = hashMapOf(
        "instant" to InstantAnimationSettings.Companion
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
        val interactableSettings = interactableSections.mapNotNull { InteractableSerializer.load(it) }

        val openRequirements =
            RequirementSerializer.fromSections<Player>(cfg.getSectionList("open-requirements")).toMutableList()
        val openPriceGroups = ArrayList<OpenPriceGroup>()

        val animationSettingsFactory = cfg.getString("animation.type", "instant")!!.lowercase()
        val factory = animationSerializers[animationSettingsFactory] ?: InstantAnimationSettings.Companion

        val rerollManager = { crate: OpenableCrate ->
            RerollManagerImpl(crate, hashMapOf())
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
            }
            BasicCrateInteractHandler(crate, clickActions)
        }

        return BasicCrate(
            identifier,
            cfg.getString("display-name") ?: identifier,
            HologramSerializer.loadAquaticHologram(cfg.getConfigurationSection("hologram")),
            interactableSettings,
            openRequirements,
            openPriceGroups,
            { bc ->
                CrateAnimationManagerImpl(
                    bc,
                    factory.serialize(cfg.getConfigurationSection("animation")),
                    rerollManager
                )
            },
            key,
            { bc ->
                val possibleRewardRanges = loadRewardRanges(cfg.getSectionList("possible-rewards"))
                val rewards = loadRewards(rewardSection)
                RewardManagerImpl(bc, possibleRewardRanges, milestoneManager, rewards)
            },
            interactHandler,
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