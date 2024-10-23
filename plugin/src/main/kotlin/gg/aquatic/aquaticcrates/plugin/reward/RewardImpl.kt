package gg.aquatic.aquaticcrates.plugin.reward

import gg.aquatic.aquaticcrates.api.hologram.AquaticHologramSettings
import gg.aquatic.aquaticcrates.api.player.CrateProfileEntry
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.api.reward.RewardAmountRange
import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import gg.aquatic.aquaticseries.lib.item2.AquaticItem
import gg.aquatic.aquaticseries.lib.requirement.ConfiguredRequirement
import org.bukkit.entity.Player

class RewardImpl(
    override val chance: Double,
    override val id: String,
    override val item: AquaticItem,
    override val giveItem: Boolean,
    override val displayName: String,
    override val globalLimits: HashMap<CrateProfileEntry.HistoryType, Int>,
    override val perPlayerLimits: HashMap<CrateProfileEntry.HistoryType, Int>,
    override val actions: List<ConfiguredAction<Player>>,
    override val requirements: List<ConfiguredRequirement<Player>>,
    override val winCrateAnimation: String?,
    override val hologramSettings: AquaticHologramSettings,
    override val amountRanges: MutableList<RewardAmountRange>
) : Reward {
}