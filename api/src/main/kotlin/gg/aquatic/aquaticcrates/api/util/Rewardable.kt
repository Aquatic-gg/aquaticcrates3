package gg.aquatic.aquaticcrates.api.util

import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticseries.lib.util.randomItem
import org.bukkit.entity.Player

interface Rewardable {

    fun getRandomRewards(player: Player, amount: Int): HashMap<String,Pair<Reward, Int>> {
        val finalRewards = HashMap<String,Pair<Reward, Int>>()
        var amountLeft = amount

        val possibleRewards = getPossibleRewards(player).values.toList()
        if (possibleRewards.isEmpty()) return finalRewards

        while (amountLeft > 0) {
            val randomReward = possibleRewards.toList().randomItem() ?: return finalRewards
            val previous = finalRewards.getOrPut(randomReward.id) { randomReward to 0 }
            finalRewards[randomReward.id] = previous.first to (previous.second + 1)
            amountLeft--
        }
        return finalRewards
    }

    fun getPossibleRewards(player: Player): HashMap<String,Reward>

}