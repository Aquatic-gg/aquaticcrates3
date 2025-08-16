package gg.aquatic.aquaticcrates.plugin.restriction.impl

import gg.aquatic.aquaticcrates.plugin.restriction.OpenData
import gg.aquatic.aquaticcrates.plugin.restriction.OpenRestriction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import org.bukkit.Material

class EmptyInventoryOpenRestriction: OpenRestriction() {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("slots",1, false)
    )

    override fun execute(binder: OpenData, args: ObjectArguments, textUpdater: (OpenData, String) -> String): Boolean {
        val slots = args.int("slots") { textUpdater(binder, it) } ?: return true
        val player = binder.player
        var count = 0
        for (storageContent in player.inventory.storageContents) {
            if (count >= slots) return true
            if (storageContent == null || storageContent.type == Material.AIR) count++
        }
        return false
    }
}