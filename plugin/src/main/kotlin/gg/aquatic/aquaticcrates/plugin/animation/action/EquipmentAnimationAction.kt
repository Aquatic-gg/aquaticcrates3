package gg.aquatic.aquaticcrates.plugin.animation.action

import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.plugin.animation.prop.EquipmentAnimationProp
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.ItemObjectArgument
import gg.aquatic.waves.util.generic.Action
import gg.aquatic.waves.util.item.toCustomItem
import org.bukkit.Material

class EquipmentAnimationAction: Action<PlayerBoundAnimation> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        ItemObjectArgument("helmet", Material.AIR.toCustomItem(), false),
        ItemObjectArgument("chestplate", Material.AIR.toCustomItem(), false),
        ItemObjectArgument("leggings", Material.AIR.toCustomItem(), false),
        ItemObjectArgument("boots", Material.AIR.toCustomItem(), false)
    )

    override fun execute(
        binder: PlayerBoundAnimation,
        args: ObjectArguments,
        textUpdater: (PlayerBoundAnimation, String) -> String
    ) {
        if (binder !is CrateAnimation) return
        val helmet = args.typed<AquaticItem>("helmet")!!
        val chestplate = args.typed<AquaticItem>("chestplate")!!
        val leggings = args.typed<AquaticItem>("leggings")!!
        val boots = args.typed<AquaticItem>("boots")!!

        val prop = EquipmentAnimationProp(helmet.getItem(), chestplate.getItem(), leggings.getItem(), boots.getItem(), binder)
        binder.props["player-equipment"] = prop

        binder.player.updateInventory()
    }
}