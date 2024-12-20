package gg.aquatic.aquaticcrates.plugin.restriction.impl

import gg.aquatic.aquaticcrates.plugin.restriction.OpenData
import gg.aquatic.aquaticcrates.plugin.restriction.OpenRestriction
import gg.aquatic.aquaticseries.lib.util.argument.AquaticObjectArgument
import gg.aquatic.aquaticseries.lib.util.argument.impl.PrimitiveObjectArgument

class PlayerOpenRestriction: OpenRestriction() {
    override fun arguments(): List<AquaticObjectArgument<*>> {
        return listOf(
            PrimitiveObjectArgument("radius",5, true)
        )
    }

    override fun check(binder: OpenData, arguments: Map<String, Any?>): Boolean {
        val radius = arguments["radius"] as? Int ?: return true
        val player = binder.player
        val location = binder.location
        val crate = binder.crate

        val playerAnimations = crate.animationManager.playingAnimations[player.uniqueId] ?: return true
        if (playerAnimations.isEmpty()) return true

        for (playerAnimation in playerAnimations) {
            if (playerAnimation.baseLocation.world != location.world) continue
            if (playerAnimation.baseLocation.distanceSquared(location) <= radius * radius) return false
        }
        return true
    }
}