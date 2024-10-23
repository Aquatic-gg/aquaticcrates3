package gg.aquatic.aquaticcrates.api.animation

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation

interface RewardVisual {

    fun create(animation: CrateAnimation): SpawnedRewardVisual

}