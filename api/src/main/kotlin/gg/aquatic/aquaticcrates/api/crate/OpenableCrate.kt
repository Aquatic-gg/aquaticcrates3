package gg.aquatic.aquaticcrates.api.crate

import gg.aquatic.aquaticcrates.api.player.HistoryHandler
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticseries.lib.chance.ChanceUtils
import org.bukkit.entity.Player

abstract class OpenableCrate: Crate() {

    abstract val key: Key
    abstract val rewards: HashMap<String,Reward>

    open fun getRandomRewards(player: Player, amount: Int): HashMap<String,Pair<Reward, Int>> {
        val finalRewards = HashMap<String,Pair<Reward, Int>>()
        var amountLeft = amount

        val possibleRewards = getPossibleRewards(player)

        while (amountLeft > 0) {
            val randomReward = ChanceUtils.getRandomItem(possibleRewards.values.toMutableList()) ?: return finalRewards
            val previous = finalRewards.getOrPut(randomReward.id) { randomReward to 0 }
            finalRewards[randomReward.id] = previous.first to (previous.second + 1)
            amountLeft--
        }
        return finalRewards
    }
    open fun getPossibleRewards(player: Player): HashMap<String,Reward> {
        val finalRewards = HashMap<String,Reward>()
        for ((id, reward) in rewards) {
            var meetsRequirements = true
            for (requirement in reward.requirements) {
                if (!requirement.check(player)) {
                    meetsRequirements = false
                    break
                }
            }
            if (!meetsRequirements) continue
            for ((type, limit) in reward.globalLimits) {
                if (HistoryHandler.history(this.identifier,id,type) >= limit) {
                    meetsRequirements = false
                    break
                }
            }
            if (!meetsRequirements) continue
            for ((type, limit) in reward.perPlayerLimits) {
                if (HistoryHandler.history(this.identifier,id,type,player) >= limit) {
                    meetsRequirements = false
                    break
                }
            }
            if (!meetsRequirements) continue
            finalRewards[id] = reward
        }

        return finalRewards
    }
}