package gg.aquatic.aquaticcrates.api.model

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

interface ModelEngineAdapter {

    fun create(id: String, location: Location, player: Player, skin: Player?)
    fun createMEGLoader(plugin: JavaPlugin, runnable: Runnable): Loader

}