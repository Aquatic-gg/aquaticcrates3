package gg.aquatic.aquaticcrates.plugin.animation.prop.block

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.aquaticseries.lib.block.AquaticBlock
import org.bukkit.util.Vector

class BlockAnimationProp(
    override val animation: Animation,
    val block: AquaticBlock,
    val offset: Vector
) : AnimationProp() {

    val packetBlock = block.placePacket(animation.baseLocation.clone().add(offset), animation.audience).apply {
        spawn()
    }

    override fun tick() {

    }

    override fun onAnimationEnd() {
        packetBlock.despawn()
    }
}