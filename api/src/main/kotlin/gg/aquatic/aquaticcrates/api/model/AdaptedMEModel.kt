package gg.aquatic.aquaticcrates.api.model

import org.bukkit.Location
import org.bukkit.entity.Player

interface AdaptedMEModel {

    fun getLocation(): Location
    fun remove()
    fun hide(player: Player)
    fun show(player: Player)
    fun hide()
    fun show()
    fun playAnimation(animation: String)

}