package gg.aquatic.aquaticcrates.api.reward.showcase

import org.bukkit.configuration.ConfigurationSection

interface RewardShowcaseFactory {

    fun load(section: ConfigurationSection): RewardShowcase?

}