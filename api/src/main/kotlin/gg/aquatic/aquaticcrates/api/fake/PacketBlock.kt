package gg.aquatic.aquaticcrates.api.fake

import org.bukkit.block.data.BlockData
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.util.Consumer

abstract class PacketBlock: FakeObject() {

    abstract val blockData: BlockData
    abstract val onInteract: Consumer<PlayerInteractEvent>

}