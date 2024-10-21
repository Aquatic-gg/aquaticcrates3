package gg.aquatic.aquaticcrates.api.reward

import gg.aquatic.aquaticcrates.api.hologram.AquaticHologramSettings
import gg.aquatic.aquaticcrates.api.player.CrateProfileEntry
import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import gg.aquatic.aquaticseries.lib.chance.IChance
import gg.aquatic.aquaticseries.lib.item2.AquaticItem
import gg.aquatic.aquaticseries.lib.requirement.ConfiguredRequirement
import org.bukkit.entity.Player

interface Reward: IChance {
    val id: String
    val item: AquaticItem
    val displayName: String
    val globalLimits: HashMap<CrateProfileEntry.HistoryType, Int>
    val perPlayerLimits: HashMap<CrateProfileEntry.HistoryType, Int>
    val actions: List<ConfiguredAction<Player>>
    val requirements: List<ConfiguredRequirement<Player>>
    val winCrateAnimation: String?
    val hologramSettings: AquaticHologramSettings
    val rewardVisual: RewardVisual
}