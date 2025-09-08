package gg.aquatic.aquaticcrates.api.reward.showcase.item

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.reward.showcase.RewardShowcase
import gg.aquatic.aquaticcrates.api.reward.showcase.RewardShowcaseFactory
import gg.aquatic.waves.hologram.HologramSerializer
import gg.aquatic.waves.interactable.settings.InteractableSettings
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.registry.serializer.ActionSerializer
import gg.aquatic.waves.registry.serializer.InteractableSerializer
import gg.aquatic.waves.util.generic.ClassTransform
import gg.aquatic.waves.util.getSectionList
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

object ItemRewardShowcaseFactory : RewardShowcaseFactory {
    override fun load(section: ConfigurationSection): RewardShowcase? {
        val item = AquaticItem.loadFromYml(section.getConfigurationSection("item"))
        val gravity = section.getBoolean("gravity")

        val hologram = if (section.isConfigurationSection("hologram")) {
            HologramSerializer.loadHologram(section.getConfigurationSection("hologram")!!)
        } else {
            HologramSerializer.loadHologram(section.getList("hologram") ?: emptyList<Any>())
        }

        val bindHologramToItem = section.getBoolean("bind-hologram-to-item", false)

        val interactables = ArrayList<InteractableSettings>()
        for (configurationSection in section.getSectionList("interactables")) {
            val interactable = InteractableSerializer.load(configurationSection) ?: continue
            interactables.add(interactable)
        }

        val spawnActions = ActionSerializer.fromSections<CrateAnimation>(
            section.getSectionList("spawn-actions"),
            ClassTransform(Player::class.java) { it.player }
        )
        val despawnActions = ActionSerializer.fromSections<CrateAnimation>(
            section.getSectionList("despawn-actions"),
            ClassTransform(Player::class.java) { it.player }
        )

        return ItemRewardShowcase(
            item,
            gravity,
            hologram,
            interactables,
            spawnActions,
            despawnActions,
            bindHologramToItem
        )
    }
}