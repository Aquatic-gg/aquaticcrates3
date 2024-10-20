package gg.aquatic.aquaticcrates.plugin

import gg.aquatic.aquaticcrates.api.AbstractCratesPlugin

class CratesPlugin: AbstractCratesPlugin() {

    //abstract val aquaticLib:

    companion object {
        val INSTANCE: CratesPlugin
            get() = AbstractCratesPlugin.INSTANCE as CratesPlugin
    }

    override fun onLoad() {
        AbstractCratesPlugin.INSTANCE = this
    }

    override fun onEnable() {
    }

}