package gg.aquatic.aquaticcrates.plugin.restriction.impl

import gg.aquatic.aquaticcrates.api.player.CrateProfileEntry
import gg.aquatic.aquaticcrates.api.player.HistoryHandler
import gg.aquatic.aquaticcrates.plugin.restriction.OpenData
import gg.aquatic.aquaticcrates.plugin.restriction.OpenRestriction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument

class GlobalLimitOpenRestriction: OpenRestriction() {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("limit", 1, true),
        PrimitiveObjectArgument("timeframe", "ALLTIME", true)
    )

    override fun execute(
        binder: OpenData,
        args: ObjectArguments,
        textUpdater: (OpenData, String) -> String
    ): Boolean {
        val limit = args.int("limit") { textUpdater(binder, it) } ?: return true
        val timeframe = args.enum<CrateProfileEntry.HistoryType>("timeframe") { textUpdater(binder, it) } ?: return true
        return HistoryHandler.history(binder.crate.identifier, timeframe) < limit
    }
}