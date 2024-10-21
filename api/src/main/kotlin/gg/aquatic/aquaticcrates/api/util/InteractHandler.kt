package gg.aquatic.aquaticcrates.api.util

import org.bukkit.entity.Player

abstract class InteractHandler {

    abstract fun handleInteract(player: Player, isLeft: Boolean)

}