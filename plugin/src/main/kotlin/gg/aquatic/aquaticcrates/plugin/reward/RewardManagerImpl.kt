package gg.aquatic.aquaticcrates.plugin.reward

import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.milestone.MilestoneManager
import gg.aquatic.aquaticcrates.api.player.CrateProfileEntry
import gg.aquatic.aquaticcrates.api.player.HistoryHandler
import gg.aquatic.aquaticcrates.api.reward.*
import gg.aquatic.waves.util.chance.randomItem
import gg.aquatic.waves.util.checkRequirements
import org.bukkit.entity.Player

class RewardManagerImpl(
    val crate: OpenableCrate,
    val possibleRewardRanges: MutableList<RewardAmountRange>,
    val guaranteedRewards: HashMap<Int,Reward>,
    milestoneManager: (OpenableCrate) -> MilestoneManager,
    override val rewards: MutableMap<String, Reward>
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

        val possibleRewards = getPossibleRewards(player)
        if (possibleRewards.isEmpty()) return finalRewards

        val alltimeHistory = HistoryHandler.history(crate.identifier, CrateProfileEntry.HistoryType.ALLTIME, player)
        if (guaranteedRewards.containsKey(alltimeHistory)) {
            val reward = guaranteedRewards[alltimeHistory]!!
            finalRewards[reward.id] = reward to 1
            amountLeft--
        }

        while (amountLeft > 0) {
            val randomReward = possibleRewards.values.toList().randomItem() ?: return finalRewards
            val previous = finalRewards.getOrPut(randomReward.id) { randomReward to 0 }
            finalRewards[randomReward.id] = previous.first to (previous.second + 1)
            amountLeft--
        }
        return finalRewards
    }

    override fun getPossibleRewards(player: Player): MutableMap<String, Reward> {
        val finalRewards = HashMap<String, Reward>()
        for ((id, reward) in rewards) {
            if (!reward.requirements.checkRequirements(player)) continue

            var meetsRequirements = true
            for ((type, limit) in reward.globalLimits) {
                if (HistoryHandler.rewardHistory(crate.identifier, id, type) >= limit) {
                    meetsRequirements = false
                    break
                }
            }
            if (!meetsRequirements) continue
            for ((type, limit) in reward.perPlayerLimits) {
                if (HistoryHandler.rewardHistory(crate.identifier, id, type, player) >= limit) {
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