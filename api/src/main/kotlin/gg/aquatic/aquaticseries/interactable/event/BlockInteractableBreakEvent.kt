package gg.aquatic.aquaticseries.interactable.event

import org.bukkit.event.block.BlockBreakEvent
import gg.aquatic.aquaticseries.interactable.impl.block.SpawnedBlockInteractable

class BlockInteractableBreakEvent(
    val originalEvent: BlockBreakEvent,
    val blockInteractable: SpawnedBlockInteractable
) {
}