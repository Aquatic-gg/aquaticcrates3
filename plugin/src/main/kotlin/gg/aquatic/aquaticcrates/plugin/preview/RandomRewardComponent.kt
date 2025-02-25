package gg.aquatic.aquaticcrates.plugin.preview

import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.plugin.crate.BasicCrate
import gg.aquatic.waves.inventory.event.AsyncPacketInventoryInteractEvent
import gg.aquatic.waves.menu.AquaticMenu
import gg.aquatic.waves.menu.MenuComponent
import gg.aquatic.waves.util.decimals
import gg.aquatic.waves.util.item.modifyFastMeta
import gg.aquatic.waves.util.toMMComponent
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
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
    val settings: CratePreviewMenuSettings
) : MenuComponent() {

    override val id: String = "random-reward:${UUID.randomUUID()}"
    var currentReward = rewards.randomOrNull()
    override fun itemstack(menu: AquaticMenu): ItemStack {
        val iS = currentReward?.item?.getItem()?.clone()
        currentReward?.let { cr ->
            iS?.modifyFastMeta {
                this.displayName = this.displayName?.let { comp ->
                    MiniMessage.miniMessage().deserialize(textUpdater(MiniMessage.miniMessage().serialize(comp), menu))
                        .decoration(TextDecoration.ITALIC, false)
                }
                this.lore = this.lore.toMutableList().apply {
                    addAll(
                        settings.additionalRewardLore.map {
                            it.toMMComponent()
                        }
                    )
                }.map {
                    MiniMessage.miniMessage().deserialize(
                        textUpdater(
                            MiniMessage.miniMessage().serialize(it)
                                .replace("%chance%", (cr.chance * 100.0).decimals(2))
                                .replace("%rarity%", cr.rarity.displayName),
                            menu
                        )
                    )
                        .decoration(TextDecoration.ITALIC, false)
                }
        }

        }
        return iS ?: ItemStack(Material.AIR)
    }

    var changeTick = 0
    override fun tick(menu: AquaticMenu) {
        if (changeEvery <= 0) return
        changeTick++
        if (changeTick >= changeEvery) {
            changeTick = 0
            currentReward = rewards.random()
            menu.updateComponent(this)
        }
    }
}