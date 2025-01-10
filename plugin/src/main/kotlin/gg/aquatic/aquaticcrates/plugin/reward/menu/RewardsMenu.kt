package gg.aquatic.aquaticcrates.plugin.reward.menu

import gg.aquatic.waves.menu.PrivateAquaticMenu
import gg.aquatic.waves.util.toMMComponent
import gg.aquatic.waves.util.updatePAPIPlaceholders
import org.bukkit.entity.Player

class RewardsMenu(val settings: RewardsMenuSettings, player: Player) : PrivateAquaticMenu(
    settings.title.updatePAPIPlaceholders(player).toMMComponent(),
    settings.type, player,
) {
}