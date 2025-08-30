package gg.aquatic.aquaticcrates.api.reward.showcase

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.reward.Reward

interface RewardShowcaseHandle<T : RewardShowcase> {

    var reward: Reward
    val showcase: T
    val animation: CrateAnimation

    fun destroy()

    fun update(settings: T, reward: Reward)

}