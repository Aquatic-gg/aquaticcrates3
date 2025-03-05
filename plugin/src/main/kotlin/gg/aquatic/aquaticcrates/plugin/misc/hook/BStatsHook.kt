package gg.aquatic.aquaticcrates.plugin.misc.hook

import gg.aquatic.aquaticcrates.plugin.CratesPlugin
import org.bstats.bukkit.Metrics

object BStatsHook {

    internal fun register() {
        Metrics(CratesPlugin.INSTANCE, 19254)
    }

}