package gg.aquatic.aquaticcrates.plugin.restriction.impl

import gg.aquatic.aquaticcrates.plugin.restriction.OpenData
import gg.aquatic.aquaticcrates.plugin.restriction.OpenRestriction
import gg.aquatic.aquaticseries.lib.util.argument.AquaticObjectArgument
import gg.aquatic.aquaticseries.lib.util.argument.impl.PrimitiveObjectArgument

class WorldBlacklistOpenRestriction: OpenRestriction() {
    override fun arguments(): List<AquaticObjectArgument<*>> {
        return listOf(
            PrimitiveObjectArgument("blacklist",ArrayList<String>(), true)
        )
    }

    override fun check(binder: OpenData, arguments: Map<String, Any?>): Boolean {
        val blacklist = arguments["blacklist"] as? List<String> ?: return true
        return binder.location.world?.name !in blacklist
    }
}