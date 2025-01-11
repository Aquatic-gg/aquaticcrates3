package gg.aquatic.aquaticcrates.plugin.animation.prop.block

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.waves.fake.block.FakeBlock
import gg.aquatic.waves.util.block.AquaticBlock
import gg.aquatic.waves.util.blockLocation
import org.bukkit.util.Vector

class MultiblockAnimationProp(
    override val animation: Animation,
    val block: AquaticBlock,
    val offsets: HashSet<Vector>,
) : AnimationProp() {

    val packetBlocks = offsets.map { offset ->
        FakeBlock(block, animation.baseLocation.clone().add(offset).blockLocation(), 50, animation.audience).apply {
            this.register()
        }
    }

    override fun tick() {

    }

    override fun onAnimationEnd() {
        for (packetBlock in packetBlocks) {
            packetBlock.destroy()
        }
    }
}