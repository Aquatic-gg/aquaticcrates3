package gg.aquatic.aquaticcrates.plugin.misc.hook

import gg.aquatic.aquaticcrates.plugin.CratesPlugin
import gg.aquatic.waves.util.registerMetrics

object BStatsHook {

    internal fun register() {
        CratesPlugin.getInstance().registerMetrics(19254)
    }

}