package gg.aquatic.aquaticcrates.api.pouche

import gg.aquatic.aquaticseries.lib.chance.IChance
import kotlin.random.Random

class PouchAmountRange(
    val min: Int,
    val max: Int,
    override val chance: Double
) : IChance {

    val randomNum: Int
        get() {
            return Random.nextInt(min, max + 1)
        }

}