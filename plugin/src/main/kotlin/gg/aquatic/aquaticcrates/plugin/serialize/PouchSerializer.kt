package gg.aquatic.aquaticcrates.plugin.serialize

import gg.aquatic.aquaticcrates.api.animation.AnimationTitle
import gg.aquatic.aquaticcrates.api.animation.pouch.PouchAnimation
import gg.aquatic.aquaticcrates.api.animation.pouch.PouchAnimationManager
import gg.aquatic.aquaticcrates.api.animation.pouch.PouchAnimationSettings
import gg.aquatic.aquaticcrates.api.openprice.OpenPriceGroup
import gg.aquatic.aquaticcrates.api.pouch.Pouch
import gg.aquatic.aquaticcrates.plugin.CratesPlugin
import gg.aquatic.aquaticcrates.plugin.animation.pouch.PouchAnimationManagerImpl
import gg.aquatic.aquaticcrates.plugin.animation.pouch.settings.PouchInstantAnimationSettings
import gg.aquatic.aquaticcrates.plugin.pouch.PouchInteractHandlerImpl
import gg.aquatic.aquaticcrates.plugin.pouch.PouchMilestoneManager
import gg.aquatic.aquaticcrates.plugin.pouch.PouchPreviewMenuSettings
import gg.aquatic.aquaticcrates.plugin.pouch.RewardPouch
import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import gg.aquatic.aquaticseries.lib.item2.AquaticItem
import gg.aquatic.aquaticseries.lib.util.Config
import gg.aquatic.aquaticseries.lib.util.getSectionList
import gg.aquatic.waves.registry.serializer.ActionSerializer
import gg.aquatic.waves.registry.serializer.InventorySerializer
import gg.aquatic.waves.registry.serializer.RequirementSerializer
import gg.aquatic.waves.util.loadFromYml
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object PouchSerializer : BaseSerializer() {

    suspend fun loadPouches(folder: File): HashMap<String, Pouch> = withContext(Dispatchers.IO) {
        val pouches = HashMap<String, Pouch>()

        for (file in folder.listFiles()!!) {
            if (file.isDirectory) {
                pouches += loadPouches(file)
                continue
            }
            val pouch = loadPouch(file) ?: continue
            pouches[pouch.identifier] = pouch
        }

        return@withContext pouches
    }

    suspend fun loadPouch(file: File): Pouch? = withContext(Dispatchers.IO) {
        sendConsoleMessage("Loading file: ${file.name}")
        val id = file.nameWithoutExtension
        val config = Config(file, CratesPlugin.INSTANCE)
        config.load()
        val cfg = config.getConfiguration()!!
        val displayName = cfg.getString("display-name") ?: id
        val item = AquaticItem.loadFromYml(cfg.getConfigurationSection("pouch-item"))
        if (item == null) {
            sendConsoleMessage("Could not load Pouch Item! (Path: pouch-item)")
            return@withContext null
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
            return@withContext null
        }

        val possibleRewardRanges = loadRewardRanges(cfg.getSectionList("possible-rewards"))
        val rewards = loadRewards(rewardSection)
        val previewSettings = loadPouchPreviewMenuSettings(cfg)

        return@withContext RewardPouch(
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
    }

    private suspend fun loadPouchPreviewMenuSettings(cfg: FileConfiguration): PouchPreviewMenuSettings =
        withContext(Dispatchers.IO) {
            val section =
                cfg.getConfigurationSection("preview") ?: return@withContext PouchPreviewMenuSettings(null, false, listOf())
            val rewardSlots = section.getIntegerList("reward-slots")
            val invSettings = InventorySerializer.loadInventory(section)
            val clearBottomInventory = section.getBoolean("clear-bottom-inventory", false)

            return@withContext PouchPreviewMenuSettings(
                invSettings,
                clearBottomInventory,
                rewardSlots,
            )
        }

    suspend fun loadPouchAnimationSettings(section: ConfigurationSection?): PouchAnimationSettings =
        withContext(Dispatchers.IO) {
            if (section == null) return@withContext PouchInstantAnimationSettings(
                TreeMap(),
                0,
                0,
                TreeMap(),
                0,
                TreeMap(),
                ArrayList(),
                false,
                AnimationTitle()
            )

            val type = section.getString("type", "instant")!!

            when (type.lowercase()) {
                "instant" -> {
                    return@withContext loadInstantAnimationSettings(section)
                }

                else -> {
                    return@withContext loadInstantAnimationSettings(section)
                }
            }
        }

    suspend fun loadInstantAnimationSettings(section: ConfigurationSection): PouchInstantAnimationSettings =
        withContext(Dispatchers.IO) {
            val animationTasks = loadAnimationTasks(section.getConfigurationSection("tasks")!!)
            val animationLength = section.getInt("animation-length", 0)
            val preAnimationTasks = loadAnimationTasks(section.getConfigurationSection("pre-animation.tasks")!!)
            val preAnimationDelay = section.getInt("pre-animation.delay", 0)
            val postAnimationTasks = loadAnimationTasks(section.getConfigurationSection("post-animation.tasks")!!)
            val postAnimationDelay = section.getInt("post-animation.delay", 0)
            val finalAnimationTasks =
                ActionSerializer.fromSections<PouchAnimation>(section.getSectionList("final-tasks")).toMutableList()
            val skippable = section.getBoolean("skippable", false)
            val openingBossbar = AnimationTitle()

            PouchInstantAnimationSettings(
                animationTasks,
                animationLength,
                preAnimationDelay,
                preAnimationTasks,
                postAnimationDelay,
                postAnimationTasks,
                finalAnimationTasks,
                skippable,
                openingBossbar
            )
        }

    suspend fun loadAnimationTasks(section: ConfigurationSection): TreeMap<Int, MutableList<ConfiguredAction<PouchAnimation>>> =
        withContext(Dispatchers.IO) {
            val tasks = TreeMap<Int, MutableList<ConfiguredAction<PouchAnimation>>>()

            for (key in section.getKeys(false)) {
                val delay = key.toIntOrNull() ?: continue
                tasks[delay] =
                    ActionSerializer.fromSections<PouchAnimation>(section.getSectionList(key)).toMutableList()
            }

            tasks
        }
}