package gg.aquatic.aquaticcrates.api.reward

import gg.aquatic.waves.util.chance.IChance

class RewardRarity(
    val rarityId: String,
    val displayName: String,
    override val chance: Double,
): IChance {
}