package gg.aquatic.aquaticcrates.plugin.reward

import gg.aquatic.aquaticcrates.api.hologram.AquaticHologramSettings
import gg.aquatic.aquaticcrates.api.player.CrateProfileEntry
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.api.reward.RewardAction
import gg.aquatic.aquaticcrates.api.reward.RewardAmountRange
import gg.aquatic.aquaticcrates.api.reward.RewardRarity
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.util.requirement.ConfiguredRequirement
import org.bukkit.entity.Player

class RewardImpl(
    override var chance: Double,
    override val id: String,
    override val item: AquaticItem,
    override val giveItem: Boolean,
    override val displayName: String,
    override val globalLimits: HashMap<CrateProfileEntry.HistoryType, Int>,
    override val perPlayerLimits: HashMap<CrateProfileEntry.HistoryType, Int>,
    override val actions: List<RewardAction>,
    override val requirements: List<ConfiguredRequirement<Player>>,
    override val winCrateAnimation: String?,
    override val hologramSettings: AquaticHologramSettings,
    override val amountRanges: MutableList<RewardAmountRange>,
    override val rarity: RewardRarity
) : Reward