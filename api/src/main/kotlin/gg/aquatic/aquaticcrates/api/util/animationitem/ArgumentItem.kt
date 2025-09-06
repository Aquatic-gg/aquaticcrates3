package gg.aquatic.aquaticcrates.api.util.animationitem

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.animation.prop.ItemBasedProp
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.scenario.Scenario
import gg.aquatic.waves.util.item.toCustomItem
import net.kyori.adventure.key.Key
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection

class ArgumentItem(
    val type: String,
    val baseItem: AquaticItem?
) {

    fun getActualItem(animation: Scenario): AquaticItem {
        if (type.lowercase().startsWith("rewarditem:")) {
            val rewardIndex = type.substringAfter("rewarditem:").toIntOrNull() ?: 0
            return if (animation is CrateAnimation) {
                (animation.rewards.getOrNull(rewardIndex) ?: animation.rewards.firstOrNull())?.reward?.item ?: baseItem ?: Material.STONE.toCustomItem()
            } else Material.STONE.toCustomItem()
        }
        if (type.lowercase().startsWith("rumbling-reward:")) {
            val rewardId = type.substringAfter("rumbling-reward:")
            return (animation.props[Key.key("rumbling-reward:$rewardId")] as? ItemBasedProp)?.item() ?: Material.STONE.toCustomItem()
        }
        if (type.lowercase() == "randomrewarditem") {
            return if (animation is CrateAnimation) {
                val crate = animation.animationManager.crate
                if (crate is OpenableCrate) {
                    val randomReward = crate.rewardManager.rewards.values.randomOrNull() ?: return Material.STONE.toCustomItem()
                    return randomReward.item
                }
                Material.STONE.toCustomItem()
            } else Material.STONE.toCustomItem()
        }
        return baseItem ?: Material.STONE.toCustomItem()
    }

    companion object {
        fun loadFromYml(section: ConfigurationSection?): ArgumentItem {
            val type = section?.getString("type") ?: "regular"
            val baseItem = AquaticItem.loadFromYml(section)
            return ArgumentItem(type, baseItem)
        }
    }

}