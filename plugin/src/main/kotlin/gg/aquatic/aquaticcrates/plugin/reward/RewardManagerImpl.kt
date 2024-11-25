package gg.aquatic.aquaticcrates.plugin.reward

import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.milestone.MilestoneManager
import gg.aquatic.aquaticcrates.api.player.HistoryHandler
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.api.reward.RewardAmountRange
import gg.aquatic.aquaticcrates.api.reward.RewardManager
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.aquaticseries.lib.util.checkRequirements
import gg.aquatic.aquaticseries.lib.util.randomItem
import org.bukkit.entity.Player

class RewardManagerImpl(
    val crate: OpenableCrate,
    val possibleRewardRanges: MutableList<RewardAmountRange>,
    milestoneManager: (OpenableCrate) -> MilestoneManager,
    override val rewards: HashMap<String, Reward>
) : RewardManager() {

    override val milestoneManager = milestoneManager(crate)
    override fun getRewards(player: Player): MutableList<RolledReward> {
        val rolledRewards = mutableListOf<RolledReward>()

        val rewardsAmount = possibleRewardRanges.randomItem()?.randomNum ?: 1
        val randomRewards = getRandomRewards(player, rewardsAmount)
        for ((_, pair) in randomRewards) {
            val reward = pair.first
            val amount = pair.second
            for (i in 0..< amount) {
                rolledRewards += RolledRewardImpl(reward,reward.amountRanges.randomItem()?.randomNum ?: 1)
            }
        }
        return rolledRewards
    }

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

    override fun getPossibleRewards(player: Player): HashMap<String, Reward> {
        val finalRewards = HashMap<String, Reward>()
        for ((id, reward) in rewards) {
            if (!reward.requirements.checkRequirements(player)) continue

            var meetsRequirements = true
            for ((type, limit) in reward.globalLimits) {
                if (HistoryHandler.history("crate:${crate.identifier}", id, type) >= limit) {
                    meetsRequirements = false
                    break
                }
            }
            if (!meetsRequirements) continue
            for ((type, limit) in reward.perPlayerLimits) {
                if (HistoryHandler.history("crate:${crate.identifier}", id, type, player) >= limit) {
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