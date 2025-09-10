package gg.aquatic.aquaticcrates.plugin.restriction.impl

import gg.aquatic.aquaticcrates.plugin.restriction.OpenData
import gg.aquatic.aquaticcrates.plugin.restriction.OpenRestriction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument

class CrateRewardsRestriction: OpenRestriction() {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("available-rewards", 1, true)
    )

    override fun execute(
        binder: OpenData,
        args: ObjectArguments,
        textUpdater: (OpenData, String) -> String
    ): Boolean {
        val availableRewards = args.int("available-rewards") { textUpdater(binder, it) } ?: return true
        return binder.crate.rewardManager.getPossibleRewards(binder.player).size >= availableRewards
    }
}