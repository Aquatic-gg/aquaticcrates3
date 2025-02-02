package gg.aquatic.aquaticcrates.plugin.animation.action.block

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.util.VectorArgument
import gg.aquatic.aquaticcrates.api.util.animationitem.BlockArgument
import gg.aquatic.aquaticcrates.plugin.animation.prop.block.BlockAnimationProp
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.block.AquaticBlock
import gg.aquatic.waves.util.block.impl.VanillaBlock
import org.bukkit.Material
import org.bukkit.util.Vector

class SetBlockAction : AbstractAction<Animation>() {

    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("id", "example", true),
        VectorArgument("offset", Vector(), false),
        BlockArgument("block", VanillaBlock(Material.STONE.createBlockData()), true)
    )

    override fun execute(binder: Animation, args: ObjectArguments, textUpdater: (Animation, String) -> String) {
        val id = args.string("id") { textUpdater(binder, it) } ?: return
        val offset = args.vector("offset") { textUpdater(binder, it) } ?: return
        val block = args.typed<AquaticBlock>("block") ?: return

        binder.props["block:$id"]?.onAnimationEnd()
        val prop = BlockAnimationProp(binder, block, offset)
        binder.props["block:$id"] = prop
    }
}