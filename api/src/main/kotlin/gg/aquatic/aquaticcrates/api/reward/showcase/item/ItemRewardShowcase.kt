package gg.aquatic.aquaticcrates.api.reward.showcase.item

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.api.reward.showcase.RewardShowcase
import gg.aquatic.aquaticcrates.api.reward.showcase.RewardShowcaseHandle
import gg.aquatic.waves.hologram.LineSettings
import gg.aquatic.waves.interactable.settings.InteractableSettings
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import org.bukkit.util.Vector
import org.joml.Vector3d

class ItemRewardShowcase(
    val item: AquaticItem?,
    val gravity: Boolean,
    override val hologram: Collection<LineSettings>,
    val hologramTranslation: Vector3d,
    override val interactables: Collection<InteractableSettings>,
    override val spawnActions: Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>,
    override val despawnActions: Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>
) : RewardShowcase {

    override fun create(
        animation: Animation,
        reward: Reward,
        locationOffset: Pair<Vector, Pair<Float, Float>>
    ): RewardShowcaseHandle<*> {
        return ItemRewardShowcaseHandle(animation, this, locationOffset, reward)
    }


}