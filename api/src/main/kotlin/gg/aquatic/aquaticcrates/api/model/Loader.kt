package gg.aquatic.aquaticcrates.api.model

import org.bukkit.event.Listener

abstract class Loader(
    val runnable: Runnable
): Listener {

    var loaded = false


}