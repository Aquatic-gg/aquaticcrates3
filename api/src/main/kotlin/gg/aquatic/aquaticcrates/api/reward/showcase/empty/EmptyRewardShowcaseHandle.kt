package gg.aquatic.aquaticcrates.api.reward.showcase.empty

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.api.reward.showcase.RewardShowcaseHandle
import gg.aquatic.waves.hologram.AquaticHologram
import org.bukkit.util.Vector

class EmptyRewardShowcaseHandle(
    override val animation: Animation,
    override var showcase: EmptyRewardShowcase,
    val locationOffset: Pair<Vector, Pair<Float, Float>>,
    var reward: Reward
) :
    RewardShowcaseHandle<EmptyRewardShowcase> {

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
            { _, str -> animation.updatePlaceholders(str) },
            50,
            showcase.hologram.map { it.create() }.toSet()
        ).apply {
            tick()
        }
    }

    override fun destroy() {
        hologram?.destroy()
        interactables.forEach { it.destroy() }
        interactables.clear()
    }

    override fun update(settings: EmptyRewardShowcase, reward: Reward) {
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
    }
}