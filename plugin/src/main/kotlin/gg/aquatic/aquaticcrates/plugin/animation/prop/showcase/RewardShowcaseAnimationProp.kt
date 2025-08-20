package gg.aquatic.aquaticcrates.plugin.animation.prop.showcase

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.api.reward.showcase.RewardShowcase
import gg.aquatic.aquaticcrates.api.reward.showcase.RewardShowcaseHandle

class RewardShowcaseAnimationProp(
    override val animation: Animation,
    val locationOffset: Pair<org.bukkit.util.Vector, Pair<Float, Float>>
) : AnimationProp() {

    var showcaseHandle: RewardShowcaseHandle<*>? = null

    override fun tick() {
    }

    override fun onAnimationEnd() {
        showcaseHandle?.destroy()
        showcaseHandle = null
    }

    fun <T : RewardShowcase> update(reward: Reward, rewardShowcase: T) {
        if (showcaseHandle?.javaClass?.genericInterfaces?.firstOrNull() == rewardShowcase.javaClass) {
            (showcaseHandle as RewardShowcaseHandle<T>).update(
                rewardShowcase, reward
            )
            return
        }
        showcaseHandle?.destroy()
        showcaseHandle = rewardShowcase.create(animation, reward, locationOffset)
    }


}