package gg.aquatic.aquaticcrates.api.reward

import gg.aquatic.aquaticcrates.api.hologram.AquaticHologramSettings
import gg.aquatic.aquaticcrates.api.player.CrateProfileEntry
import gg.aquatic.aquaticcrates.api.player.crateEntry
import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import gg.aquatic.aquaticseries.lib.chance.IChance
import gg.aquatic.aquaticseries.lib.requirement.ConfiguredRequirement
import gg.aquatic.aquaticseries.lib.util.addOrDropItem
import gg.aquatic.aquaticseries.lib.util.displayName
import gg.aquatic.aquaticseries.lib.util.executeActions
import gg.aquatic.aquaticseries.lib.util.runSync
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.profile.toAquaticPlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface Reward: IChance {
    val id: String
    val item: AquaticItem
    val giveItem: Boolean
    val displayName: String
    val globalLimits: HashMap<CrateProfileEntry.HistoryType, Int>
    val perPlayerLimits: HashMap<CrateProfileEntry.HistoryType, Int>
    val actions: List<RewardAction>
    val requirements: List<ConfiguredRequirement<Player>>
    val winCrateAnimation: String?
    val hologramSettings: AquaticHologramSettings
    val amountRanges: MutableList<RewardAmountRange>

    fun give(player: Player, randomAmount: Int, massOpen: Boolean) {
        val crateEntry = player.toAquaticPlayer()?.crateEntry() ?: return
        if (giveItem) {
            val item = this.item.getItem()
            val toDrop = player.inventory.addItem(item)

            for (value in toDrop.values) {
                var foundItem: Pair<ItemStack,Int>? = null
                for ((containerItem, amount) in crateEntry.rewardContainer.items) {
                    if (containerItem.isSimilar(value)) {
                        foundItem = containerItem to amount
                        break
                    }
                }
                if (foundItem != null) {
                    val newAmount = foundItem.second + value.amount
                    crateEntry.rewardContainer.items[foundItem.first] = newAmount
                    //player.sendMessage("Currently got ${newAmount}x ${foundItem.first.type} in Reward Container")
                } else {
                    crateEntry.rewardContainer.items[value] = value.amount
                    //player.sendMessage("Currently got ${randomAmount}x ${value.type} in Reward Container")
                }
            }

        }
        for (action in actions) {
            if (!action.massOpenExecute && massOpen) continue
            action.action.run(player) { p, str -> str.replace("%player%", p.name).replace("%random-amount%", randomAmount.toString()) }
        }
    }
}