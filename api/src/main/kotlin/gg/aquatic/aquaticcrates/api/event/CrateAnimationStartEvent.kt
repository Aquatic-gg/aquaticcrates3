package gg.aquatic.aquaticcrates.api.event

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.waves.api.event.AquaticEvent
import org.bukkit.entity.Player

class CrateAnimationStartEvent(
    val crate: OpenableCrate,
    val animation: CrateAnimation,
    val player: Player
): AquaticEvent(true) {
}