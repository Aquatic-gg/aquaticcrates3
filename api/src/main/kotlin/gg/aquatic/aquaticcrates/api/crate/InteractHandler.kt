package gg.aquatic.aquaticcrates.api.crate

import org.bukkit.entity.Player

abstract class InteractHandler {

    abstract val crate: Crate

    abstract fun handleInteract(player: Player, isLeft: Boolean)

}