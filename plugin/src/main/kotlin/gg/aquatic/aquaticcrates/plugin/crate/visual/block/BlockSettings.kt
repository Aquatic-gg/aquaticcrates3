package gg.aquatic.aquaticcrates.plugin.crate.visual.block

import org.bukkit.block.BlockFace

class BlockSettings(
    val rotation: BlockFace,
    val allowedBlockFaces: List<BlockFace>,
    val upPlacement: Boolean
) {
}