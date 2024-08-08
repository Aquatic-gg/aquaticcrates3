package gg.aquatic.aquaticcrates.plugin.crate.visual.block

import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.crate.visual.CrateVisual
import gg.aquatic.aquaticcrates.api.crate.visual.VisualHandler
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.data.Directional
import org.bukkit.entity.Player
import java.util.UUID

class BlockVisual(
    override val location: Location,
    val material: Material,
    val blockSettings: BlockSettings,
    override val crate: Crate,
) : CrateVisual() {

    val spawned = ArrayList<UUID>()

    override fun spawn(player: Player) {
        despawn(player)
        val blockData = material.createBlockData()

        when (blockData) {
            is Directional -> {
                blockData.facing = blockSettings.rotation
            }
        }

        player.sendBlockChange(location, blockData)
        spawned += player.uniqueId
    }

    override fun despawn(player: Player) {
        player.sendBlockChange(location, location.block.blockData)
        spawned -= player.uniqueId
    }

    override fun handler(): BlockVisualHandler {
        return HANDLER
    }

    companion object {

        val HANDLER = BlockVisualHandler()

    }
}