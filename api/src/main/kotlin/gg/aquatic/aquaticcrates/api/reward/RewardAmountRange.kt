package gg.aquatic.aquaticcrates.api.reward

import gg.aquatic.aquaticseries.lib.chance.IChance
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class RewardAmountRange(
    val min: Int,
    val max: Int,
    override val chance: Double
) : IChance {

    val randomNum: Int
        get() {
            if (min == max) {
                return min
            }
            return Random.nextInt(min(min, max), max(min, max) + 1)
        }

}