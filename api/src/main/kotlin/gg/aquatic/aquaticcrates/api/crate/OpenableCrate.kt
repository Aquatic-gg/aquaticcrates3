package gg.aquatic.aquaticcrates.api.crate

import gg.aquatic.aquaticcrates.api.openprice.OpenPriceGroup
import gg.aquatic.aquaticcrates.api.player.HistoryHandler
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.api.util.Rewardable
import gg.aquatic.aquaticseries.lib.requirement.ConfiguredRequirement
import gg.aquatic.aquaticseries.lib.util.checkRequirements
import org.bukkit.entity.Player

abstract class OpenableCrate : Crate(), Rewardable {

    abstract val key: Key
    abstract val openRequirements: MutableList<ConfiguredRequirement<Player>>
    abstract val openPriceGroups: MutableList<OpenPriceGroup>
    abstract val skipAnimationWhileSneaking: Boolean

    abstract fun canBeOpened(player: Player): Boolean

    abstract val historyNamespace: String

    override fun getPossibleRewards(player: Player): HashMap<String, Reward> {
        val finalRewards = HashMap<String, Reward>()
        for ((id, reward) in rewards) {
            if (!reward.requirements.checkRequirements(player)) continue

            var meetsRequirements = true
            for ((type, limit) in reward.globalLimits) {
                if (HistoryHandler.history("crate:$identifier", id, type) >= limit) {
                    meetsRequirements = false
                    break
                }
            }
            if (!meetsRequirements) continue
            for ((type, limit) in reward.perPlayerLimits) {
                if (HistoryHandler.history("crate:$identifier", id, type, player) >= limit) {
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