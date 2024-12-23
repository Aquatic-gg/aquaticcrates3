package gg.aquatic.aquaticcrates.api.reward

import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import org.bukkit.entity.Player

class RewardAction(
    val massOpenExecute: Boolean,
    val action: ConfiguredExecutableObject<Player,Unit>
) {
}