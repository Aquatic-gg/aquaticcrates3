package gg.aquatic.aquaticcrates.plugin.serialize

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.pouch.PouchAnimationManager
import gg.aquatic.aquaticcrates.api.animation.pouch.PouchAnimationSettings
import gg.aquatic.aquaticcrates.api.openprice.OpenPriceGroup
import gg.aquatic.aquaticcrates.api.pouch.Pouch
import gg.aquatic.aquaticcrates.plugin.CratesPlugin
import gg.aquatic.aquaticcrates.plugin.animation.pouch.PouchAnimationManagerImpl
import gg.aquatic.aquaticcrates.plugin.animation.pouch.settings.PouchInstantAnimationSettings
import gg.aquatic.aquaticcrates.plugin.animation.pouch.settings.PouchRegularAnimationSettings
import gg.aquatic.aquaticcrates.plugin.pouch.PouchMilestoneManager
import gg.aquatic.aquaticcrates.plugin.pouch.PouchPreviewMenuSettings
import gg.aquatic.aquaticcrates.plugin.pouch.RewardPouch
import gg.aquatic.aquaticseries.lib.util.Config
import gg.aquatic.aquaticseries.lib.util.getSectionList
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.item.loadFromYml
import gg.aquatic.waves.registry.serializer.ActionSerializer
import gg.aquatic.waves.registry.serializer.InventorySerializer
import gg.aquatic.waves.registry.serializer.RequirementSerializer
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object PouchSerializer : BaseSerializer() {

    fun loadPouches(folder: File): HashMap<String, Pouch> {
        val pouches = HashMap<String, Pouch>()

        for (file in folder.listFiles()!!) {
            if (file.isDirectory) {
                pouches += loadPouches(file)
                continue
            }
            val pouch = loadPouch(file) ?: continue
            pouches[pouch.identifier] = pouch
        }

        return pouches
    }

    fun loadPouch(file: File): Pouch? {
        sendConsoleMessage("Loading file: ${file.name}")
        val id = file.nameWithoutExtension
        val config = Config(file, CratesPlugin.INSTANCE)
        config.load()
        val cfg = config.getConfiguration()!!
        val displayName = cfg.getString("display-name") ?: id
        val item = AquaticItem.loadFromYml(cfg.getConfigurationSection("pouch-item"))
        if (item == null) {
            sendConsoleMessage("Could not load Pouch Item! (Path: pouch-item)")
            return null
        }
        val openRequirements =
            RequirementSerializer.fromSections<Player>(cfg.getSectionList("open-requirements")).toMutableList()
        val openPriceGroups = ArrayList<OpenPriceGroup>()
        val animationSettings = loadPouchAnimationSettings(cfg.getConfigurationSection("animation"))
        val animationManager: (Pouch) -> PouchAnimationManager = { pouch ->
            PouchAnimationManagerImpl(pouch, animationSettings)
        }

        val rewardSection = cfg.getConfigurationSection("rewards")
        if (rewardSection == null) {
            sendConsoleMessage("Could not load Rewards! (Path: rewards)")
            return null
        }

        val possibleRewardRanges = loadRewardRanges(cfg.getSectionList("possible-rewards"))
        val rewards = loadRewards(rewardSection)
        val previewSettings = loadPouchPreviewMenuSettings(cfg)
        /*
        return RewardPouch(
            id,
            item,
            displayName,
            openRequirements,
            openPriceGroups,
            animationManager,
            { p -> PouchInteractHandlerImpl(p) },
            rewards,
            possibleRewardRanges,
            previewSettings,
            PouchMilestoneManager(TreeMap(), TreeMap())
        )
         */
        return null
    }

    private fun loadPouchPreviewMenuSettings(cfg: FileConfiguration): PouchPreviewMenuSettings {
        val section =
            cfg.getConfigurationSection("preview") ?: return PouchPreviewMenuSettings(null, false, listOf())
        val rewardSlots = section.getIntegerList("reward-slots")
        val invSettings = InventorySerializer.loadInventory(section)
        val clearBottomInventory = section.getBoolean("clear-bottom-inventory", false)

        return PouchPreviewMenuSettings(
            invSettings,
            clearBottomInventory,
            rewardSlots,
        )
    }

    fun loadPouchAnimationSettings(section: ConfigurationSection?): PouchAnimationSettings {
        if (section == null) return PouchInstantAnimationSettings(
            TreeMap(),
            0,
            0,
            TreeMap(),
            0,
            TreeMap(),
            ArrayList(),
            false,
        )

        val type = section.getString("type", "instant")!!

        return when (type.lowercase()) {
            "instant" -> {
                loadInstantAnimationSettings(section)
            }

            "regular" -> {
                loadRegularAnimationSettings(section)
            }

            else -> {
                loadInstantAnimationSettings(section)
            }
        }
    }

    fun loadInstantAnimationSettings(section: ConfigurationSection): PouchInstantAnimationSettings {
        val animationTasks = loadAnimationTasks(section.getConfigurationSection("tasks")!!)
        val animationLength = section.getInt("animation.length", 0)
        val preAnimationTasks = loadAnimationTasks(section.getConfigurationSection("pre-animation.tasks")!!)
        val preAnimationDelay = section.getInt("pre-animation.delay", 0)
        val postAnimationTasks = loadAnimationTasks(section.getConfigurationSection("post-animletation.tasks")!!)
        val postAnimationDelay = section.getInt("post-animation.delay", 0)
        val finalAnimationTasks =
            ActionSerializer.fromSections<Animation>(section.getSectionList("final-tasks")).toMutableList()
        val skippable = section.getBoolean("skippable", false)

        return PouchInstantAnimationSettings(
            animationTasks,
            animationLength,
            preAnimationDelay,
            preAnimationTasks,
            postAnimationDelay,
            postAnimationTasks,
            finalAnimationTasks,
            skippable,
        )
    }

    fun loadRegularAnimationSettings(section: ConfigurationSection): PouchRegularAnimationSettings {
        val animationTasks = loadAnimationTasks(section.getConfigurationSection("tasks"))
        val animationLength = section.getInt("length", 0)
        val preAnimationTasks = loadAnimationTasks(section.getConfigurationSection("pre-animation.tasks"))
        val preAnimationDelay = section.getInt("pre-animation.delay", 0)
        val postAnimationTasks = loadAnimationTasks(section.getConfigurationSection("post-animation.tasks"))
        val postAnimationDelay = section.getInt("post-animation.delay", 0)
        val finalAnimationTasks =
            ActionSerializer.fromSections<Animation>(section.getSectionList("final-tasks")).toMutableList()
        val skippable = section.getBoolean("skippable", false)
        val personal = section.getBoolean("personal", false)

        return PouchRegularAnimationSettings(
            animationTasks,
            animationLength,
            preAnimationDelay,
            preAnimationTasks,
            postAnimationDelay,
            postAnimationTasks,
            finalAnimationTasks,
            skippable,
            personal
        )
    }
}