package gg.aquatic.aquaticcrates.api.reward.showcase.item

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.api.reward.showcase.RewardShowcaseHandle
import gg.aquatic.waves.fake.entity.FakeEntity
import gg.aquatic.waves.fake.entity.data.impl.ItemEntityData
import gg.aquatic.waves.fake.entity.data.impl.living.BaseEntityData
import gg.aquatic.waves.hologram.AquaticHologram
import gg.aquatic.waves.interactable.type.EntityInteractable
import gg.aquatic.waves.util.collection.mapPair
import org.bukkit.entity.EntityType
import org.bukkit.util.Vector

class ItemRewardShowcaseHandle(
    override val animation: Animation,
    override var showcase: ItemRewardShowcase,
    val locationOffset: Pair<Vector, Pair<Float, Float>>,
    override var reward: Reward
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
        if (showcase.hologram.isEmpty()) return null
        return AquaticHologram(
            animation.baseLocation.clone().add(locationOffset.first).apply {
                this.yaw = locationOffset.second.first
                this.pitch = locationOffset.second.second
            },
            { p -> animation.audience.canBeApplied(p) },
            { _, str -> animation.updatePlaceholders(reward.updatePlaceholders(str)) },
            50,
            showcase.hologram.map { it.create() }.toSet()
        ).apply {
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
        hologram?.destroy()
        this.showcase = settings
        hologram = initializeHologram()
        interactables.forEach { it.destroy() }
        interactables.clear()

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