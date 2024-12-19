package gg.aquatic.aquaticcrates.plugin.preview

import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.plugin.crate.BasicCrate
import gg.aquatic.waves.inventory.event.AsyncPacketInventoryInteractEvent
import gg.aquatic.waves.item.modifyFastMeta
import gg.aquatic.waves.menu.AquaticMenu
import gg.aquatic.waves.menu.MenuComponent
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.inventory.ItemStack
import java.util.UUID

class RandomRewardComponent(
    val crate: BasicCrate,
    val rewards: Collection<Reward>,
    val changeEvery: Int,
    override var onClick: (AsyncPacketInventoryInteractEvent) -> Unit,
    override val priority: Int,
    override val slots: Collection<Int>,
    val textUpdater: (String, AquaticMenu) -> String,
) : MenuComponent() {

    override val id: String = "random-reward:${UUID.randomUUID()}"
    var currentRewardItem = rewards.random().item.getItem()
    override fun itemstack(menu: AquaticMenu): ItemStack {
        val iS = currentRewardItem.clone()
        iS.modifyFastMeta {
            this.displayName = this.displayName?.let { comp ->
                MiniMessage.miniMessage().deserialize(textUpdater(MiniMessage.miniMessage().serialize(comp), menu))
            }
            this.lore = this.lore.map {
                MiniMessage.miniMessage().deserialize(textUpdater(MiniMessage.miniMessage().serialize(it), menu))
            }
        }
        return iS
    }

    var changeTick = 0
    override fun tick(menu: AquaticMenu) {
        if (changeEvery <= 0) return
        changeTick++
        if (changeTick >= changeEvery) {
            changeTick = 0
            currentRewardItem = rewards.random().item.getItem()
            menu.updateComponent(this)
        }
    }
}