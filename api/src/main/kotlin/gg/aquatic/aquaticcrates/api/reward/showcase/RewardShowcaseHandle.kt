package gg.aquatic.aquaticcrates.api.reward.showcase

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.reward.Reward

interface RewardShowcaseHandle<T : RewardShowcase> {

    var reward: Reward
    val showcase: T
    val animation: Animation

    fun destroy()

    fun update(settings: T, reward: Reward)

}