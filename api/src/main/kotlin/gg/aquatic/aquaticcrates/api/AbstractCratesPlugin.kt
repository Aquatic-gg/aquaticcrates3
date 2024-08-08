package gg.aquatic.aquaticcrates.api

import org.bukkit.plugin.java.JavaPlugin

abstract class AbstractCratesPlugin: JavaPlugin() {

    companion object {
        lateinit var INSTANCE: AbstractCratesPlugin
    }

}