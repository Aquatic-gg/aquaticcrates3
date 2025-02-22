package gg.aquatic.aquaticcrates.plugin.restriction.impl

import gg.aquatic.aquaticcrates.plugin.restriction.OpenData
import gg.aquatic.aquaticcrates.plugin.restriction.OpenRestriction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import org.bukkit.Material

class EmptyInventoryOpenRestriction: OpenRestriction() {
    override val arguments: List<AquaticObjectArgument<*>> = listOf()

    override fun execute(binder: OpenData, args: ObjectArguments, textUpdater: (OpenData, String) -> String): Boolean {
        val player = binder.player
        for (storageContent in player.inventory.storageContents) {
            if (storageContent == null || storageContent.type == Material.AIR) return true
        }
        return false
    }
}