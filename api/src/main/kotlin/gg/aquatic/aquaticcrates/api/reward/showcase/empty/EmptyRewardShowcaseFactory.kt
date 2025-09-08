package gg.aquatic.aquaticcrates.api.reward.showcase.empty

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.reward.showcase.RewardShowcase
import gg.aquatic.aquaticcrates.api.reward.showcase.RewardShowcaseFactory
import gg.aquatic.waves.hologram.HologramSerializer
import gg.aquatic.waves.interactable.settings.InteractableSettings
import gg.aquatic.waves.registry.serializer.ActionSerializer
import gg.aquatic.waves.registry.serializer.InteractableSerializer
import gg.aquatic.waves.util.generic.ClassTransform
import gg.aquatic.waves.util.getSectionList
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

object EmptyRewardShowcaseFactory : RewardShowcaseFactory {
    override fun load(section: ConfigurationSection): RewardShowcase? {
        val hologram = if (section.isConfigurationSection("hologram")) {
            HologramSerializer.loadHologram(section.getConfigurationSection("hologram")!!)
        } else {
            HologramSerializer.loadHologram(section.getList("hologram") ?: emptyList<Any>())
        }

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

        return EmptyRewardShowcase(
            hologram,
            interactables,
            spawnActions,
            despawnActions
        )
    }
}