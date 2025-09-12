package gg.aquatic.aquaticcrates.api.reward

import gg.aquatic.aquaticcrates.api.AbstractCratesPlugin
import gg.aquatic.aquaticcrates.api.player.CrateProfileEntry
import gg.aquatic.aquaticcrates.api.player.crateEntry
import gg.aquatic.aquaticcrates.api.reward.showcase.RewardShowcase
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.profile.toAquaticPlayer
import gg.aquatic.waves.util.chance.IChance
import gg.aquatic.waves.util.decimals
import gg.aquatic.waves.util.requirement.ConfiguredRequirement
import gg.aquatic.waves.util.updatePAPIPlaceholders
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface Reward : IChance {
    val id: String
    val item: AquaticItem
    val giveItem: Boolean
    val displayName: String
    val globalLimits: HashMap<CrateProfileEntry.HistoryType, Int>
    val perPlayerLimits: HashMap<CrateProfileEntry.HistoryType, Int>
    val actions: List<RewardAction>
    val requirements: List<ConfiguredRequirement<Player>>

    //val hologramSettings: AquaticHologramSettings
    val amountRanges: MutableList<RewardAmountRange>
    val rarity: RewardRarity
    val variables: MutableMap<String, String>
    val previewFallbackItem: AquaticItem?
    val showcase: RewardShowcase?

    fun give(player: Player, randomAmount: Int, massOpen: Boolean) {
        val crateEntry = player.toAquaticPlayer()?.crateEntry() ?: return
        if (giveItem) {
            val item = this.item.getItem()
            val toDrop = player.inventory.addItem(item)

            for (value in toDrop.values) {
                if (AbstractCratesPlugin.INSTANCE.settings.useRewardsMenu) {
                    var foundItem: Pair<ItemStack, Int>? = null
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
                } else {
                    player.world.dropItem(player.location, value)
                }
            }
        }
        for (action in actions) {
            if (!action.massOpenExecute && massOpen) continue
            action.action.execute(player) { p, str ->
                updatePlaceholders(str).updatePAPIPlaceholders(player).replace("%player%", p.name)
                    .replace("%random-amount%", randomAmount.toString())
            }
        }
    }

    fun updatePlaceholders(str: String): String {
        var finalStr = str
        variables.forEach { (key, value) ->
            finalStr = finalStr.replace("%reward-var:$key%", value)
        }
        return finalStr
            .replace("%reward-name%", displayName)
            .replace("%chance%", (chance * 100.0).decimals(2))
            .replace("%rarity-name%", rarity.displayName)
            .replace("%rarity-id%", rarity.rarityId)
    }
}