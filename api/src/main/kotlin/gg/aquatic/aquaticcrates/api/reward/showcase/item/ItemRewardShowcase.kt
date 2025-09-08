package gg.aquatic.aquaticcrates.api.reward.showcase.item

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.api.reward.showcase.RewardShowcase
import gg.aquatic.aquaticcrates.api.reward.showcase.RewardShowcaseHandle
import gg.aquatic.waves.hologram.AquaticHologram
import gg.aquatic.waves.interactable.settings.InteractableSettings
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import org.bukkit.util.Vector

class ItemRewardShowcase(
    val item: AquaticItem?,
    val gravity: Boolean,
    override val hologram: AquaticHologram.Settings?,
    override val interactables: Collection<InteractableSettings>,
    override val spawnActions: Collection<ConfiguredExecutableObject<CrateAnimation, Unit>>,
    override val despawnActions: Collection<ConfiguredExecutableObject<CrateAnimation, Unit>>,
    val bindHologramToItem: Boolean
) : RewardShowcase {

    override fun create(
        animation: CrateAnimation,
        reward: Reward,
        locationOffset: Pair<Vector, Pair<Float, Float>>
    ): RewardShowcaseHandle<*> {
        return ItemRewardShowcaseHandle(animation, this, locationOffset, reward)
    }


}