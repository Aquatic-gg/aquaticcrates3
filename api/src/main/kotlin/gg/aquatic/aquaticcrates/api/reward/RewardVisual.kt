package gg.aquatic.aquaticcrates.api.reward

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation

interface RewardVisual {

    fun create(animation: CrateAnimation): SpawnedRewardVisual

}