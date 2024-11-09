package gg.aquatic.aquaticcrates.plugin.serialize

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.hologram.AquaticHologramSettings
import gg.aquatic.aquaticcrates.api.player.CrateProfileEntry
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.api.reward.RewardAmountRange
import gg.aquatic.aquaticcrates.plugin.reward.RewardImpl
import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import gg.aquatic.aquaticseries.lib.betterhologram.AquaticHologram.Billboard
import gg.aquatic.aquaticseries.lib.util.getSectionList
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.item.loadFromYml
import gg.aquatic.waves.registry.serializer.ActionSerializer
import gg.aquatic.waves.registry.serializer.HologramSerializer
import gg.aquatic.waves.registry.serializer.RequirementSerializer
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.*

abstract class BaseSerializer {

    fun loadRewards(section: ConfigurationSection): HashMap<String, Reward> {
        val rewards = HashMap<String, Reward>()

        for (key in section.getKeys(false)) {
            val rewardSection = section.getConfigurationSection(key) ?: continue
            val reward = loadReward(rewardSection) ?: continue
            rewards[key] = reward
        }

        return rewards
    }

    fun loadReward(section: ConfigurationSection): Reward? {
        val id = section.name
        val item = AquaticItem.loadFromYml(section.getConfigurationSection("item"))
        if (item == null) {
            sendConsoleMessage("Could not load Reward Item! (${section.currentPath}.item)")
            return null
        }
        val chance = section.getDouble("chance", 1.0)
        val giveItem = section.getBoolean("give-item", false)
        val displayName = section.getString("display-name") ?: id
        val globalLimits: HashMap<CrateProfileEntry.HistoryType, Int> = HashMap()
        val perPlayerLimits: HashMap<CrateProfileEntry.HistoryType, Int> = HashMap()
        val actions = ActionSerializer.fromSections<Player>(section.getSectionList("actions"))
        val requirements = RequirementSerializer.fromSections<Player>(section.getSectionList("requirements"))
        val winCrateAnimation = section.getString("win-crate-animation")
        val hologramSettings = loadAquaticHologram(section.getConfigurationSection("hologram"))
        val chances = loadRewardRanges(section.getSectionList("amount-ranges"))

        return RewardImpl(chance, id, item, giveItem, displayName, globalLimits, perPlayerLimits, actions, requirements, winCrateAnimation, hologramSettings, chances)
    }

    fun sendConsoleMessage(vararg message: String) {
        message.forEach {
            Bukkit.getConsoleSender().sendMessage(it)
        }
    }

    fun loadAquaticHologram(section: ConfigurationSection?): AquaticHologramSettings {
        section ?: return AquaticHologramSettings(
            ArrayList(),
            Vector(0, 0, 0),
            Billboard.CENTER
        )
        val offset =
            section.getString("offset", "0;0;0")!!.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val billboard: Billboard = Billboard.valueOf(section.getString("billboard", "CENTER")!!.uppercase(Locale.getDefault()))
        val vector = Vector(
            offset[0].toDouble(),
            offset[1].toDouble(),
            offset[2].toDouble()
        )
        val lines = HologramSerializer.load(section.getSectionList("lines"))
        return AquaticHologramSettings(lines, vector, billboard)
    }

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

    fun loadAnimationTasks(section: ConfigurationSection?): TreeMap<Int, MutableList<ConfiguredAction<Animation>>> {
            val tasks = TreeMap<Int, MutableList<ConfiguredAction<Animation>>>()
            if (section == null) return tasks

            for (key in section.getKeys(false)) {
                val delay = key.toIntOrNull() ?: continue
                tasks[delay] =
                    ActionSerializer.fromSections<Animation>(section.getSectionList(key)).toMutableList()
            }

            return tasks
        }

}