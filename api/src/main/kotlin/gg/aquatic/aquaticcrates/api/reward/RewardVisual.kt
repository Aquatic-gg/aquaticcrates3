package gg.aquatic.aquaticcrates.api.reward

import gg.aquatic.aquaticcrates.api.animation.Animation

interface RewardVisual {

    fun create(animation: Animation): SpawnedRewardVisual

}