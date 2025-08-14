package gg.aquatic.aquaticcrates.plugin.restriction.impl

import gg.aquatic.aquaticcrates.plugin.restriction.OpenData
import gg.aquatic.aquaticcrates.plugin.restriction.OpenRestriction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument

class WorldBlacklistOpenRestriction: OpenRestriction() {

    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("blacklist",ArrayList<String>(), true)
    )

    override fun execute(binder: OpenData, args: ObjectArguments, textUpdater: (OpenData, String) -> String): Boolean {
        val blacklist = args.stringOrCollection("blacklist") { textUpdater(binder, it) } ?: return true
        if (binder.location == null) return true
        return binder.location.world?.name !in blacklist
    }
}