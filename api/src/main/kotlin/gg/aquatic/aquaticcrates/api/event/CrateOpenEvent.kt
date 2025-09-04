package gg.aquatic.aquaticcrates.api.event

import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.waves.api.event.AquaticEvent
import org.bukkit.entity.Player

class CrateOpenEvent(
    val crate: OpenableCrate,
    val player: Player,
    val amount: Int = 1
): AquaticEvent(true) {
}