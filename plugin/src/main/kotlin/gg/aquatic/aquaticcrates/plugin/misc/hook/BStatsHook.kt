package gg.aquatic.aquaticcrates.plugin.misc.hook

import gg.aquatic.waves.util.BStatsUtils

object BStatsHook {

    internal fun register() {
        BStatsUtils.registerMetrics(19254)
    }

}