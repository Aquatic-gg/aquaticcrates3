package gg.aquatic.aquaticcrates.api.animation

import org.bukkit.entity.Player

abstract class PlayerBoundAnimation: Animation() {

    abstract val player: Player

}