package gg.aquatic.aquaticcrates.api.animation

import gg.aquatic.aquaticcrates.api.reward.Reward

interface SpawnedRewardVisual {

    fun change(reward: Reward)
    fun despawn()

}