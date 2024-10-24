package gg.aquatic.aquaticcrates.api.reward

import org.bukkit.entity.Player

abstract class RolledReward {

    abstract val reward: Reward
    abstract val randomAmount: Int

    abstract fun give(player: Player)
}