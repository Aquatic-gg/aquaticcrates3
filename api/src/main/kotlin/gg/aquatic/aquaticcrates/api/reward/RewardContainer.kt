package gg.aquatic.aquaticcrates.api.reward

import org.bukkit.inventory.ItemStack
import java.util.concurrent.ConcurrentHashMap

class RewardContainer {

    val items = ConcurrentHashMap<ItemStack, Int>()

}