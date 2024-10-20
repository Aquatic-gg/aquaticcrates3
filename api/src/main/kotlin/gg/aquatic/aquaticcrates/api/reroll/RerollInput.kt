package gg.aquatic.aquaticcrates.api.reroll

import gg.aquatic.aquaticcrates.api.reward.Reward
import org.bukkit.entity.Player

interface RerollInput {

    fun handle(rerollManager: RerollManager, player: Player, reward: Reward)

}