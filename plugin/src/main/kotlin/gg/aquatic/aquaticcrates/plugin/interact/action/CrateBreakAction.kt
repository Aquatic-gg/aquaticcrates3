package gg.aquatic.aquaticcrates.plugin.interact.action

import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import gg.aquatic.aquaticcrates.api.interaction.CrateInteractAction
import gg.aquatic.aquaticcrates.plugin.Bootstrap
import gg.aquatic.waves.util.action.RegisterAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.generic.Action
import gg.aquatic.waves.util.task.AsyncCtx

@RegisterAction("destroy-crate")
class CrateBreakAction: Action<CrateInteractAction> {

    override val arguments: List<AquaticObjectArgument<*>> = listOf()

    override fun execute(
        binder: CrateInteractAction,
        args: ObjectArguments,
        textUpdater: (CrateInteractAction, String) -> String
    ) {
        if (!binder.player.hasPermission("aquaticcrates.admin")) return
        val spawnedCrate = binder.spawnedCrate ?: return
        AsyncCtx {
            spawnedCrate.destroy()
            CrateHandler.spawned -= spawnedCrate.location
            CrateHandler.saveSpawnedCrates(Bootstrap.spawnedCratesConfig)
        }

    }
}