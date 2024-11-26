package gg.aquatic.aquaticcrates.plugin.preview

import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.plugin.crate.BasicCrate
import gg.aquatic.aquaticseries.lib.betterinventory2.SlotSelection
import gg.aquatic.aquaticseries.lib.betterinventory2.component.InventoryComponent
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import java.util.UUID
import java.util.function.BiFunction
import java.util.function.Consumer
import java.util.function.Function

class RandomRewardComponent(
    val crate: BasicCrate,
    val rewards: Collection<Reward>,
    val changeEvery: Int,
    override var onClick: Consumer<InventoryClickEvent>?,
    override val priority: Int,
    override val slotSelection: SlotSelection,
    override val textUpdater: BiFunction<Player, String, String>,
) : InventoryComponent() {

    override val id: String = "random-reward:${UUID.randomUUID()}"
    override val updateEvery: Int = changeEvery

    var currentRewardItem = rewards.random().item.getItem()
    override val item: ItemStack
        get() {
            return currentRewardItem
        }

    override val failItem: InventoryComponent? = null
    override val viewConditions: HashMap<Function<Player, Boolean>, InventoryComponent?> = HashMap()
    var changeTick = 0
    override fun tick() {
        if (changeEvery <= 0) return
        changeTick++
        if (changeTick >= changeEvery) {
            changeTick = 0
            currentRewardItem = rewards.random().item.getItem()
        }
    }
}