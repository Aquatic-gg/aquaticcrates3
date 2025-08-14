package gg.aquatic.aquaticcrates.plugin.restriction.impl

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.plugin.restriction.OpenData
import gg.aquatic.aquaticcrates.plugin.restriction.OpenRestriction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument

class AllGlobalOpenRestriction : OpenRestriction() {

    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("radius", 5, true)
    )

    override fun execute(binder: OpenData, args: ObjectArguments, textUpdater: (OpenData, String) -> String): Boolean {
        val radius = args.int("radius") { textUpdater(binder, it) } ?: return true
        val location = binder.location ?: return true

        val playerAnimations = mutableListOf<CrateAnimation>()
        for ((_, cr) in CrateHandler.crates) {
            if (cr !is OpenableCrate) continue
            cr.animationManager.playingAnimations.values.forEach { playerAnimations.addAll(it) }
        }
        if (playerAnimations.isEmpty()) return true

        for (playerAnimation in playerAnimations) {
            if (playerAnimation.baseLocation.world != location.world) continue
            if (playerAnimation.baseLocation.distanceSquared(location) <= radius * radius) return false
        }
        return true
    }

}