package gg.aquatic.aquaticcrates.api

import org.bukkit.plugin.java.JavaPlugin

abstract class AbstractCratesPlugin: JavaPlugin() {

    companion object {
        lateinit var INSTANCE: AbstractCratesPlugin
    }

    abstract val settings: PluginSettings

}

class PluginSettings(
    var useRewardsMenu: Boolean,
    var logOpenings: Boolean,
    var openingsThreshold: Int
)