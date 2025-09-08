package gg.aquatic.aquaticcrates.api.reward.showcase.item

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.api.reward.showcase.RewardShowcaseHandle
import gg.aquatic.waves.fake.entity.FakeEntity
import gg.aquatic.waves.fake.entity.data.impl.ItemEntityData
import gg.aquatic.waves.fake.entity.data.impl.living.BaseEntityData
import gg.aquatic.waves.hologram.AquaticHologram
import gg.aquatic.waves.hologram.line.AnimatedHologramLine
import gg.aquatic.waves.hologram.line.ItemHologramLine
import gg.aquatic.waves.hologram.line.TextHologramLine
import gg.aquatic.waves.interactable.type.EntityInteractable
import gg.aquatic.waves.util.collection.mapPair
import org.bukkit.entity.EntityType
import org.bukkit.util.Vector

class ItemRewardShowcaseHandle(
    override val animation: CrateAnimation,
    override var showcase: ItemRewardShowcase,
    val locationOffset: Pair<Vector, Pair<Float, Float>>,
    override var reward: Reward,
) :
    RewardShowcaseHandle<ItemRewardShowcase> {

    val interactable = EntityInteractable(
        FakeEntity(
            EntityType.ITEM,
            animation.baseLocation.clone().add(locationOffset.first).apply {
                this.yaw = locationOffset.second.first
                this.pitch = locationOffset.second.second
            },
            50,
            animation.audience,
            consumer = {
                val newItem = showcase.item?.getItem() ?: reward.item.getItem()
                this.entityData += (BaseEntityData.HasGravity.generate(showcase.gravity) + ItemEntityData.Item.generate(
                    newItem
                )).mapPair { it.id to it }
            },
            {}, {}
        )) {}

    val interactables = showcase.interactables.map {
        it.build(
            animation.baseLocation.clone().add(locationOffset.first).apply {
                this.yaw = locationOffset.second.first
                this.pitch = locationOffset.second.second
            },
            animation.audience
        ) {}
    }.toMutableList()

    var hologram = initializeHologram()

    fun initializeHologram(): AquaticHologram? {
        if (showcase.hologram == null) return null
        return showcase.hologram!!.create(
            animation.baseLocation.clone().add(locationOffset.first).apply {
                this.yaw = locationOffset.second.first
                this.pitch = locationOffset.second.second
            },
            { _, str -> animation.updatePlaceholders(reward.updatePlaceholders(str)) },
            { p -> animation.audience.canBeApplied(p) }
        ).apply {
            if (showcase.bindHologramToItem) {
                this.setAsPassenger(interactable.entity.entityId)
            }
            tick()
        }
    }

    override fun destroy() {
        hologram?.destroy()
        interactable.destroy()
        interactables.forEach { it.destroy() }
        interactables.clear()
    }

    override fun update(settings: ItemRewardShowcase, reward: Reward) {
        this.reward = reward

        interactables.forEach { it.destroy() }
        interactables.clear()

        val newHologram = settings.hologram
        val previousHologram = showcase.hologram

        var wasHologramHandled = false
        if (hologram != null && newHologram != null && previousHologram != null) {
            if (newHologram.lines.size == previousHologram.lines.size) {
                var areLinesIdentical = true
                for ((index, lineSettings) in newHologram.lines.withIndex()) {
                    val oldLineSettings = previousHologram.lines[index]
                    if (oldLineSettings.javaClass != lineSettings.javaClass) {
                        areLinesIdentical = false
                        break
                    }
                }
                if (areLinesIdentical) {
                    wasHologramHandled = true

                    for ((index, line) in hologram!!.lines.withIndex()) {
                        val lineSettings = newHologram.lines[index]
                        when (line) {
                            is TextHologramLine -> line.text = (lineSettings as TextHologramLine.Settings).text
                            is ItemHologramLine -> line.setItem((lineSettings as ItemHologramLine.Settings).item)
                            is AnimatedHologramLine -> {
                                line.frames.clear()
                                line.frames += (lineSettings as AnimatedHologramLine.Settings).frames.map { it.first to it.second.create() }
                            }
                        }
                    }
                }
            }
        }
        if (!wasHologramHandled) {
            hologram?.destroy()
            this.showcase = settings
            hologram = initializeHologram()
        }

        interactables += settings.interactables.map {
            it.build(
                animation.baseLocation.clone().add(locationOffset.first).apply {
                    this.yaw = locationOffset.second.first
                    this.pitch = locationOffset.second.second
                },
                animation.audience
            ) {}
        }

        interactable.entity.updateEntity {
            val newItem = settings.item?.getItem() ?: reward.item.getItem()
            this.entityData += (BaseEntityData.HasGravity.generate(settings.gravity) + ItemEntityData.Item.generate(
                newItem
            )).mapPair { it.id to it }
        }
    }
}