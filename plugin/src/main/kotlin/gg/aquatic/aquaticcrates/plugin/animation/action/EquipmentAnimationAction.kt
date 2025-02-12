package gg.aquatic.aquaticcrates.plugin.animation.action

import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.ItemObjectArgument
import gg.aquatic.waves.util.generic.Action

class EquipmentAnimationAction: Action<PlayerBoundAnimation> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        ItemObjectArgument("helmet", null, false),
        ItemObjectArgument("chestplate", null, false),
        ItemObjectArgument("leggings", null, false),
        ItemObjectArgument("boots", null, false),
        ItemObjectArgument("hand", null, false),
        ItemObjectArgument("offhand", null, false),
        ItemObjectArgument("hotbar", null, false)
    )

    override fun execute(
        binder: PlayerBoundAnimation,
        args: ObjectArguments,
        textUpdater: (PlayerBoundAnimation, String) -> String
    ) {
        if (binder !is CrateAnimation) return
        val helmet = args.typed<AquaticItem>("helmet")
        val chestplate = args.typed<AquaticItem>("chestplate")
        val leggings = args.typed<AquaticItem>("leggings")
        val boots = args.typed<AquaticItem>("boots")
        val hand = args.typed<AquaticItem>("hand")
        val offhand = args.typed<AquaticItem>("offhand")
        val hotbar = args.typed<AquaticItem>("hotbar")

        helmet?.getItem()?.let {
            binder.playerEquipment[CrateAnimation.EquipmentSlot.HELMET] = it
        }
        chestplate?.getItem()?.let {
            binder.playerEquipment[CrateAnimation.EquipmentSlot.CHESTPLATE] = it
        }
        leggings?.getItem()?.let {
            binder.playerEquipment[CrateAnimation.EquipmentSlot.LEGGINGS] = it
        }
        boots?.getItem()?.let {
            binder.playerEquipment[CrateAnimation.EquipmentSlot.BOOTS] = it
        }
        hand?.getItem()?.let {
            binder.playerEquipment[CrateAnimation.EquipmentSlot.HAND] = it
        }
        offhand?.getItem()?.let {
            binder.playerEquipment[CrateAnimation.EquipmentSlot.OFFHAND] = it
        }
        hotbar?.getItem()?.let {
            val entries = listOf(
                CrateAnimation.EquipmentSlot.NUM_0,
                CrateAnimation.EquipmentSlot.NUM_1,
                CrateAnimation.EquipmentSlot.NUM_2,
                CrateAnimation.EquipmentSlot.NUM_3,
                CrateAnimation.EquipmentSlot.NUM_4,
                CrateAnimation.EquipmentSlot.NUM_5,
                CrateAnimation.EquipmentSlot.NUM_6,
                CrateAnimation.EquipmentSlot.NUM_7,
                CrateAnimation.EquipmentSlot.NUM_8,
            )
            for (entry in entries) {
                binder.playerEquipment[entry] = it
            }
        }

        binder.player.updateInventory()
    }
}