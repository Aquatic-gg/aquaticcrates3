package gg.aquatic.aquaticcrates.plugin

import gg.aquatic.aquaticcrates.api.AbstractCratesPlugin
import gg.aquatic.aquaticcrates.api.fake.FakeHandler

abstract class CratesPlugin: AbstractCratesPlugin() {

    //abstract val aquaticLib:

    companion object {
        val INSTANCE: CratesPlugin
            get() = AbstractCratesPlugin.INSTANCE as CratesPlugin
    }

    override fun onLoad() {
        AbstractCratesPlugin.INSTANCE = this
    }

    override fun onEnable() {
        FakeHandler.registerListeners(this)
    }

}