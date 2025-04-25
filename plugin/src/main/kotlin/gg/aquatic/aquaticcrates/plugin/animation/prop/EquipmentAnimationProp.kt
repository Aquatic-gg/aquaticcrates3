package gg.aquatic.aquaticcrates.plugin.animation.prop

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import org.bukkit.inventory.ItemStack

class EquipmentAnimationProp(
    val helmet: ItemStack,
    val chestplate: ItemStack,
    val leggings: ItemStack,
    val boots: ItemStack, override val animation: CrateAnimation
): AnimationProp() {

    override fun tick() {
    }

    override fun onAnimationEnd() {
        animation.player.updateInventory()
    }
}