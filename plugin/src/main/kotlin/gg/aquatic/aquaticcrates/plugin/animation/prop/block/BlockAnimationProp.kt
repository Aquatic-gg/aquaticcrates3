package gg.aquatic.aquaticcrates.plugin.animation.prop.block

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.waves.fake.block.FakeBlock
import gg.aquatic.waves.util.block.AquaticBlock
import gg.aquatic.waves.util.blockLocation
import org.bukkit.util.Vector

class BlockAnimationProp(
    override val animation: Animation,
    val block: AquaticBlock,
    val offset: Vector
) : AnimationProp() {

    val packetBlock = FakeBlock(block, animation.baseLocation.clone().add(offset).blockLocation(), 50, animation.audience).apply {
        this.register()
    }

    override fun tick() {

    }

    override fun onAnimationEnd() {
        packetBlock.destroy()
    }
}