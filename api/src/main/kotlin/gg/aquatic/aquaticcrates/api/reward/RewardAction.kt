package gg.aquatic.aquaticcrates.api.reward

import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import org.bukkit.entity.Player

class RewardAction(
    val massOpenExecute: Boolean,
    val action: ConfiguredAction<Player>
) {
}