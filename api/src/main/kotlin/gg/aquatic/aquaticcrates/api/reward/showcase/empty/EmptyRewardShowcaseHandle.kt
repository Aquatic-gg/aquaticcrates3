package gg.aquatic.aquaticcrates.api.reward.showcase.empty

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.api.reward.showcase.RewardShowcaseHandle
import gg.aquatic.waves.hologram.AquaticHologram
import gg.aquatic.waves.hologram.line.AnimatedHologramLine
import gg.aquatic.waves.hologram.line.ItemHologramLine
import gg.aquatic.waves.hologram.line.TextHologramLine
import org.bukkit.util.Vector

class EmptyRewardShowcaseHandle(
    override val animation: CrateAnimation,
    override var showcase: EmptyRewardShowcase,
    val locationOffset: Pair<Vector, Pair<Float, Float>>,
    override var reward: Reward
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
        if (showcase.hologram == null) return null
        return showcase.hologram!!.create(
            animation.baseLocation.clone().add(locationOffset.first).apply {
                this.yaw = locationOffset.second.first
                this.pitch = locationOffset.second.second
            },
            { _, str -> animation.updatePlaceholders(reward.updatePlaceholders(str)) },
            { p -> animation.audience.canBeApplied(p) }
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
    }
}