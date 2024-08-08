package gg.aquatic.aquaticcrates.plugin.crate.visual.block

import gg.aquatic.aquaticcrates.api.crate.visual.VisualHandler
import gg.aquatic.aquaticcrates.plugin.CratesPlugin
import gg.aquatic.aquaticcrates.plugin.crate.visual.meg.MEGVisualHandler.Listeners
import org.bukkit.Bukkit
import org.bukkit.event.Listener

class BlockVisualHandler: VisualHandler() {

    init {
        registerListeners()
    }

    private fun registerListeners() {
        val inst = CratesPlugin.INSTANCE
        Bukkit.getServer().pluginManager.registerEvents(gg.aquatic.aquaticcrates.plugin.crate.visual.meg.MEGVisualHandler.Listeners(), inst)
    }

    class Listeners: Listener {

    }

}