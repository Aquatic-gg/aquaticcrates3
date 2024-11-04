package gg.aquatic.aquaticcrates.api.util.animationitem

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.item.toCustomItem
import gg.aquatic.waves.util.loadFromYml
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection

class ArgumentItem(
    val type: String,
    val baseItem: AquaticItem?
) {

    fun getActualItem(animation: Animation): AquaticItem {
        if (type.startsWith("rewarditem:")) {
            val rewardIndex = type.substringAfter("rewarditem:").toIntOrNull() ?: 0
            val item = (animation.rewards.getOrNull(rewardIndex) ?: animation.rewards.firstOrNull())?.reward?.item ?: baseItem ?: Material.STONE.toCustomItem()
            return item
        }
        return baseItem ?: Material.STONE.toCustomItem()
    }

    companion object {
        suspend fun loadFromYml(section: ConfigurationSection?): ArgumentItem {
            val type = section?.getString("type") ?: "regular"
            val baseItem = AquaticItem.loadFromYml(section)
            return ArgumentItem(type, baseItem)
        }
    }

}