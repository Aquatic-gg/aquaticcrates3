package gg.aquatic.aquaticcrates.api.reward

import gg.aquatic.aquaticcrates.api.AbstractCratesPlugin
import gg.aquatic.aquaticcrates.api.player.CrateProfileEntry
import gg.aquatic.aquaticcrates.api.player.crateEntry
import gg.aquatic.aquaticcrates.api.reward.showcase.RewardShowcase
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.profile.toAquaticPlayer
import gg.aquatic.waves.util.chance.IChance
import gg.aquatic.waves.util.collection.executeActions
import gg.aquatic.waves.util.decimals
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import gg.aquatic.waves.util.requirement.ConfiguredRequirement
import gg.aquatic.waves.util.updatePAPIPlaceholders
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.math.BigInteger
import kotlin.math.min

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
    val massOpenFinalActions: Collection<ConfiguredExecutableObject<Player, Unit>>

    fun massGive(player: Player, totalAmount: BigInteger, uniqueAmount: Int) {
        val crateEntry = player.toAquaticPlayer()?.crateEntry() ?: return
        if (giveItem) {
            val item = this.item.getItem()
            val totalItemAmount = item.amount * uniqueAmount
            val maxAmount = min(64, totalItemAmount)
            item.amount = maxAmount

            val toDrop = HashMap<Int, ItemStack>()
            if (totalItemAmount > maxAmount) {
                var remaining = totalItemAmount
                var foundDrops = false
                while(true) {
                    val newItem = item.clone()
                    val toDecrease = min(remaining, maxAmount)
                    newItem.amount = toDecrease
                    remaining -= toDecrease

                    val drops = player.inventory.addItem(newItem)
                    if (drops.isNotEmpty()) {
                        foundDrops = true
                        break
                    }

                    toDrop[maxAmount] = newItem
                    if (remaining == 0) break
                }

                if (foundDrops) {
                    toDrop[remaining] = item
                }
            }

            toDrop += player.inventory.addItem(item)

            for ((amt, value) in toDrop) {
                if (AbstractCratesPlugin.INSTANCE.settings.useRewardsMenu) {
                    var foundItem: Pair<ItemStack, Int>? = null
                    for ((containerItem, amount) in crateEntry.rewardContainer.items) {
                        if (containerItem.isSimilar(value)) {
                            foundItem = containerItem to amount
                            break
                        }
                    }
                    if (foundItem != null) {
                        val newAmount = foundItem.second + amt
                        crateEntry.rewardContainer.items[foundItem.first] = newAmount
                        //player.sendMessage("Currently got ${newAmount}x ${foundItem.first.type} in Reward Container")
                    } else {
                        crateEntry.rewardContainer.items[value] = amt
                        //player.sendMessage("Currently got ${randomAmount}x ${value.type} in Reward Container")
                    }
                }
            }
        }
        massOpenFinalActions.executeActions(player) { p, str ->
            updatePlaceholders(str).updatePAPIPlaceholders(p).replace("%total-amount%", totalAmount.toString())
                .replace("%amount%", uniqueAmount.toString())
        }
    }

    fun give(player: Player, randomAmount: Int, massOpen: Boolean) {
        if (!massOpen && giveItem) {
            val crateEntry = player.toAquaticPlayer()?.crateEntry() ?: return
            val item = this.item.getItem()
            item.amount *= randomAmount
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