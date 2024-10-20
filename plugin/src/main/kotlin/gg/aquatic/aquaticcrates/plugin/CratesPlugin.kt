package gg.aquatic.aquaticcrates.plugin

import gg.aquatic.aquaticcrates.api.AbstractCratesPlugin

class CratesPlugin: AbstractCratesPlugin() {

    override fun onLoad() {
        INSTANCE = this
    }

    override fun onEnable() {
    }

}