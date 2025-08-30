package gg.aquatic.aquaticcrates.api.reward.showcase.empty

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.api.reward.showcase.RewardShowcase
import gg.aquatic.aquaticcrates.api.reward.showcase.RewardShowcaseHandle
import gg.aquatic.waves.hologram.LineSettings
import gg.aquatic.waves.interactable.settings.InteractableSettings
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import org.bukkit.util.Vector
import org.joml.Vector3d

class EmptyRewardShowcase(
    override val hologram: Collection<LineSettings>,
    val hologramTranslation: Vector3d,
    override val interactables: Collection<InteractableSettings>,
    override val spawnActions: Collection<ConfiguredExecutableObject<CrateAnimation, Unit>>,
    override val despawnActions: Collection<ConfiguredExecutableObject<CrateAnimation, Unit>>
) : RewardShowcase {

    override fun create(
        animation: CrateAnimation,
        reward: Reward,
        locationOffset: Pair<Vector, Pair<Float, Float>>
    ): RewardShowcaseHandle<*> {
        return EmptyRewardShowcaseHandle(animation, this, locationOffset, reward)
    }


}