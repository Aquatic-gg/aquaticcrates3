package gg.aquatic.aquaticcrates.api.reward.showcase

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.waves.hologram.LineSettings
import gg.aquatic.waves.interactable.settings.InteractableSettings
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import org.bukkit.util.Vector

interface RewardShowcase {

    val hologram: Collection<LineSettings>
    val interactables: Collection<InteractableSettings>
    val spawnActions: Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>
    val despawnActions: Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>

    fun create(animation: Animation, reward: Reward, locationOffset: Pair<Vector, Pair<Float, Float>>): RewardShowcaseHandle<*>

}