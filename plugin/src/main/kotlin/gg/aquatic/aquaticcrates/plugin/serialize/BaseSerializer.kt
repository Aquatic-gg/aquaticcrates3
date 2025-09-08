package gg.aquatic.aquaticcrates.plugin.serialize

import gg.aquatic.aquaticcrates.api.player.CrateProfileEntry
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.api.reward.RewardAction
import gg.aquatic.aquaticcrates.api.reward.RewardAmountRange
import gg.aquatic.aquaticcrates.api.reward.RewardRarity
import gg.aquatic.aquaticcrates.api.reward.showcase.RewardShowcaseSerializer
import gg.aquatic.aquaticcrates.plugin.reward.RewardImpl
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.registry.serializer.ActionSerializer
import gg.aquatic.waves.registry.serializer.ItemSerializer
import gg.aquatic.waves.registry.serializer.RequirementSerializer
import gg.aquatic.waves.util.getSectionList
import gg.aquatic.waves.util.runSync
import gg.aquatic.waves.util.toMMString
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

abstract class BaseSerializer {

    fun loadRewards(
        section: ConfigurationSection,
        rarities: HashMap<String, RewardRarity>
    ): MutableMap<String, Reward> {
        val rewards = LinkedHashMap<String, Reward>()

        for (key in section.getKeys(false)) {
            val rewardSection = section.getConfigurationSection(key) ?: continue
            val reward = loadReward(rewardSection, rarities) ?: continue
            rewards[reward.id] = reward
            Bukkit.getConsoleSender().sendMessage("Loaded Reward: ${reward.id}")
        }

        val totalRarityChance = rarities.values.sumOf { it.chance }
        val normalizedRarityChances = rarities.values.associate { it.rarityId to it.chance / totalRarityChance }

        for (rarity in rarities) {
            val rarityRewards = ArrayList<Reward>()
            for ((_, reward) in rewards) {
                if (reward.rarity == rarity.value) {
                    rarityRewards += reward
                }
            }

            val totalRewardChance = rarityRewards.sumOf { it.chance }
            val normalizedRewards = rarityRewards.map { it to it.chance / totalRewardChance }

            normalizedRewards.forEach { (reward, normalizedRewardChance) ->
                val rarityChance = normalizedRarityChances[rarity.key] ?: 0.0
                if (reward is RewardImpl) {
                    reward.chance = rarityChance * normalizedRewardChance
                }
            }
        }

        Bukkit.getConsoleSender().sendMessage("Loaded Rewards:")
        rewards.forEach { (_, reward) ->
            Bukkit.getConsoleSender().sendMessage("- ${reward.id} (Chance: ${reward.chance})")
        }

        return rewards
    }

    fun loadReward(section: ConfigurationSection, rarities: HashMap<String, RewardRarity>): Reward? {
        val id = section.name
        val itemFuture = CompletableFuture<AquaticItem?>()
        runSync {
            itemFuture.complete(AquaticItem.loadFromYml(section.getConfigurationSection("item")))
        }
        val item = itemFuture.join()
        if (item == null) {
            sendConsoleMessage("Could not load Reward Item! (${section.currentPath}.item)")
            return null
        }
        val chance = section.getDouble("chance", 1.0)
        val giveItem = section.getBoolean("give-item", false)
        val globalLimits: HashMap<CrateProfileEntry.HistoryType, Int> = HashMap()
        val perPlayerLimits: HashMap<CrateProfileEntry.HistoryType, Int> = HashMap()
        section.getConfigurationSection("global-limits")?.getKeys(false)?.forEach { key ->
            val limit = section.getInt("global-limits.$key")
            globalLimits[CrateProfileEntry.HistoryType.valueOf(key.uppercase())] = limit
        }
        section.getConfigurationSection("per-player-limits")?.getKeys(false)?.forEach { key ->
            val limit = section.getInt("per-player-limits.$key")
            perPlayerLimits[CrateProfileEntry.HistoryType.valueOf(key.uppercase())] = limit
        }

        val actions = ArrayList<RewardAction>()
        val actionSections = section.getSectionList("actions")
        for (actionSection in actionSections) {
            val massOpenExecute = actionSection.getBoolean("mass-open-execute", true)
            val action = ActionSerializer.fromSection<Player>(actionSection) ?: continue
            actions += RewardAction(massOpenExecute, action)
        }

        val requirementSections = ArrayList<ConfigurationSection>()
        requirementSections += section.getSectionList("requirements")
        requirementSections += section.getSectionList("conditions")
        val requirements = RequirementSerializer.fromSections<Player>(requirementSections)
        //val hologramSettings = HologramSerializer.loadAquaticHologram(section.getConfigurationSection("hologram"))
        val chances = loadRewardRanges(section.getSectionList("amount-ranges"))

        val rarityId = section.getString("rarity") ?: "default"
        val rarity = rarities[rarityId] ?: return null
        val displayName = section.getString("display-name")

        val variables = HashMap<String, String>()
        section.getConfigurationSection("variables")?.getKeys(false)?.forEach { key ->
            variables[key] = section.getString("variables.$key") ?: return@forEach
        }
        val fallbackPreviewItem = ItemSerializer.fromSection(section.getConfigurationSection("fallback-preview-item"))
        val showcase = section.getConfigurationSection("showcase")?.let {
            RewardShowcaseSerializer.load(it)
        }

        return RewardImpl(
            chance,
            id,
            item,
            giveItem,
            displayName ?: item.getItem().itemMeta?.displayName()?.toMMString() ?: id,
            globalLimits,
            perPlayerLimits,
            actions,
            requirements,
            chances,
            rarity,
            variables,
            fallbackPreviewItem,
            showcase
        )
    }

    fun sendConsoleMessage(vararg message: String) {
        message.forEach {
            Bukkit.getConsoleSender().sendMessage(it)
        }
    }

    /*
    fun loadAquaticHologram(section: ConfigurationSection?): AquaticHologramSettings {
        section ?: return AquaticHologramSettings(
            HashSet(),
            Vector(0, 0, 0)
        )
        val offset =
            section.getString("offset", "0;0;0")!!.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val billboard: Billboard =
            Billboard.valueOf(section.getString("billboard", "CENTER")!!.uppercase(Locale.getDefault()))
        val vector = Vector(
            offset[0].toDouble(),
            offset[1].toDouble(),
            offset[2].toDouble()
        )
        val lines = HologramSerializer.load(section.getSectionList("lines"))
        return AquaticHologramSettings(lines, vector, billboard)
    }

     */

    fun loadRewardRanges(sections: List<ConfigurationSection>): MutableList<RewardAmountRange> {
        if (sections.isEmpty()) return mutableListOf()
        val list = mutableListOf<RewardAmountRange>()
        for (rangeSection in sections) {
            val min = rangeSection.getInt("min")
            val max = rangeSection.getInt("max")
            val chance = rangeSection.getDouble("chance", 1.0)
            list.add(RewardAmountRange(min, max, chance))
        }
        return list
    }

    /*
    fun loadAnimationTasks(section: ConfigurationSection?): TreeMap<Int, MutableList<ConfiguredExecutableObject<Animation, Unit>>> {
        val tasks = TreeMap<Int, MutableList<ConfiguredExecutableObject<Animation, Unit>>>()
        if (section == null) return tasks

        for (key in section.getKeys(false)) {
            val delay = key.toIntOrNull() ?: continue
            tasks[delay] =
                ActionSerializer.fromSections<Animation>(section.getSectionList(key)).toMutableList()
        }

        return tasks
    }
     */

}