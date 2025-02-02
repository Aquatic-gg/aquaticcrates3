package gg.aquatic.aquaticcrates.plugin.animation.action.block

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.util.VectorListArgument
import gg.aquatic.aquaticcrates.api.util.animationitem.BlockArgument
import gg.aquatic.aquaticcrates.plugin.animation.prop.block.MultiblockAnimationProp
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.block.AquaticBlock
import gg.aquatic.waves.util.block.impl.VanillaBlock
import org.bukkit.Material
import org.bukkit.util.Vector

class SetMultiblockAction : AbstractAction<Animation>() {

    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("id", "example", true),
        VectorListArgument("offsets", listOf(), false),
        BlockArgument("block", VanillaBlock(Material.STONE.createBlockData()), true)
    )

    override fun execute(binder: Animation, args: ObjectArguments, textUpdater: (Animation, String) -> String) {
        val id = args.string("id") { textUpdater(binder, it) } ?: return
        val offsets = args.typed<List<Vector>>("offsets") ?: return
        val block = args.typed<AquaticBlock>("block") ?: return

        binder.props["multiblock:$id"]?.onAnimationEnd()
        val prop = MultiblockAnimationProp(binder, block, offsets.toHashSet())
        binder.props["multiblock:$id"] = prop
    }
}