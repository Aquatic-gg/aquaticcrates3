package gg.aquatic.aquaticcrates.api.reward.showcase.item

import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.api.reward.showcase.RewardShowcase
import gg.aquatic.aquaticcrates.api.reward.showcase.RewardShowcaseFactory
import gg.aquatic.waves.hologram.HologramSerializer
import gg.aquatic.waves.interactable.settings.InteractableSettings
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.registry.serializer.ActionSerializer
import gg.aquatic.waves.registry.serializer.InteractableSerializer
import gg.aquatic.waves.util.generic.ClassTransform
import gg.aquatic.waves.util.getSectionList
import gg.aquatic.waves.util.item.loadFromYml
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.joml.Vector3d

object ItemRewardShowcaseFactory : RewardShowcaseFactory {
    override fun load(section: ConfigurationSection): RewardShowcase? {
        val item = AquaticItem.loadFromYml(section.getConfigurationSection("item"))
        val gravity = section.getBoolean("gravity")
        val hologram = HologramSerializer.loadLines(section.getSectionList("hologram"))
        val hologramTranslation = section.getString("hologram-translation")?.split(";")?.let {
            Vector3d(
                it[0].toDouble(),
                it[1].toDouble(),
                it[2].toDouble()
            )
        }

        val interactables = ArrayList<InteractableSettings>()
        for (configurationSection in section.getSectionList("interactables")) {
            val interactable = InteractableSerializer.load(configurationSection) ?: continue
            interactables.add(interactable)
        }

        val spawnActions = ActionSerializer.fromSections<PlayerBoundAnimation>(
            section.getSectionList("spawn-actions"),
            ClassTransform(Player::class.java) { it.player }
        )
        val despawnActions = ActionSerializer.fromSections<PlayerBoundAnimation>(
            section.getSectionList("despawn-actions"),
            ClassTransform(Player::class.java) { it.player }
        )

        return ItemRewardShowcase(
            item,
            gravity,
            hologram,
            hologramTranslation ?: Vector3d(0.0, 0.0, 0.0),
            interactables,
            spawnActions,
            despawnActions
        )
    }
}