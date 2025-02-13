package gg.aquatic.aquaticcrates.plugin.animation.action.block

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.util.VectorListArgument
import gg.aquatic.aquaticcrates.api.util.animationitem.BlockArgument
import gg.aquatic.aquaticcrates.plugin.animation.prop.block.BlockAnimationProp
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.block.AquaticBlock
import gg.aquatic.waves.util.block.impl.VanillaBlock
import gg.aquatic.waves.util.generic.Action
import org.bukkit.Material
import org.bukkit.util.Vector

class SetMultiblockAction : Action<Animation> {

    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        //PrimitiveObjectArgument("id", "example", true),
        VectorListArgument("offsets", listOf(), false),
        BlockArgument("block", VanillaBlock(Material.STONE.createBlockData()), true)
    )

    override fun execute(binder: Animation, args: ObjectArguments, textUpdater: (Animation, String) -> String) {
        //val id = args.string("id") { textUpdater(binder, it) } ?: return
        val offsets = args.typed<List<Vector>>("offsets") ?: return
        val block = args.typed<AquaticBlock>("block") ?: return

        for (offset in offsets) {
            val offsetStr = "${offset.x.toInt()};${offset.y.toInt()};${offset.z.toInt()}"

            binder.props["block:$offsetStr"]?.onAnimationEnd()
            val prop = BlockAnimationProp(binder, block, offset)
            binder.props["block:$offsetStr"] = prop
        }
    }
}