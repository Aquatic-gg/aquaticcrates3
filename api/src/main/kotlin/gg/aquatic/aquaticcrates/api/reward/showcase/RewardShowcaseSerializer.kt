package gg.aquatic.aquaticcrates.api.reward.showcase

import gg.aquatic.aquaticcrates.api.reward.showcase.empty.EmptyRewardShowcaseFactory
import gg.aquatic.aquaticcrates.api.reward.showcase.item.ItemRewardShowcaseFactory
import org.bukkit.configuration.ConfigurationSection

object RewardShowcaseSerializer {

    val factories = mutableMapOf(
        "item" to ItemRewardShowcaseFactory,
        "empty" to EmptyRewardShowcaseFactory,
    )

    fun load(section: ConfigurationSection): RewardShowcase? {
        val type = section.getString("type")?.lowercase() ?: return null
        val factory = factories[type] ?: return null
        return factory.load(section)
    }
}