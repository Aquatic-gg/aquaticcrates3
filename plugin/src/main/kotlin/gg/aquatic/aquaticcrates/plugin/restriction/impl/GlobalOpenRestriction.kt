package gg.aquatic.aquaticcrates.plugin.restriction.impl

import gg.aquatic.aquaticcrates.plugin.restriction.OpenData
import gg.aquatic.aquaticcrates.plugin.restriction.OpenRestriction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument

class GlobalOpenRestriction: OpenRestriction() {

    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("radius",5, true)
    )

    override fun execute(
        binder: OpenData,
        args: Map<String, Any?>,
        textUpdater: (OpenData, String) -> String
    ): Boolean {
        val radius = args["radius"] as? Int ?: return true
        val location = binder.location
        val crate = binder.crate

        val playerAnimations = crate.animationManager.playingAnimations.values
        if (playerAnimations.isEmpty()) return true

        for (animations in playerAnimations) {
            for (animation in animations) {
                if (animation.baseLocation.world != location.world) continue
                if (animation.baseLocation.distanceSquared(location) <= radius * radius) return false
            }
        }
        return true
    }
}