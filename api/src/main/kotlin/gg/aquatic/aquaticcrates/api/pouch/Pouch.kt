package gg.aquatic.aquaticcrates.api.pouch

import gg.aquatic.aquaticcrates.api.animation.pouch.PouchAnimationManager
import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import gg.aquatic.aquaticcrates.api.openprice.OpenPriceGroup
import gg.aquatic.aquaticcrates.api.player.HistoryHandler
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.api.reward.RewardAmountRange
import gg.aquatic.aquaticcrates.api.util.Rewardable
import gg.aquatic.aquaticseries.lib.item2.AquaticItem
import gg.aquatic.aquaticseries.lib.requirement.ConfiguredRequirement
import gg.aquatic.aquaticseries.lib.util.checkRequirements
import gg.aquatic.waves.item.ItemHandler
import gg.aquatic.waves.registry.register
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

abstract class Pouch(
    val identifier: String,
    val item: AquaticItem
): Rewardable {

    companion object {
        fun get(id: String): Pouch? {
            return CrateHandler.pouches[id]
        }
        fun get(itemStack: ItemStack): Pouch? {
            val meta = itemStack.itemMeta ?: return null
            val pair =
                meta.persistentDataContainer.get(ItemHandler.NAMESPACE_KEY, PersistentDataType.STRING) ?: return null
            val id = pair.substringAfter(":")
            val namespace = pair.substringBefore(":")
            if (!namespace.equals("pouch", true)) return null
            return get(
                id
            )
        }
    }

    abstract val displayName: String
    abstract val openRequirements: MutableList<ConfiguredRequirement<Player>>
    abstract val openPriceGroups: MutableList<OpenPriceGroup>
    abstract val animationManager: PouchAnimationManager
    abstract val interactHandler: PouchInteractHandler
    abstract val rewards: HashMap<String,Pair<Reward,MutableList<RewardAmountRange>>>

    init {
        item.register("aquaticcrates", "pouch:$identifier") {
            interactHandler.handleInteract(it.player, it.isLeftClick)
            it.isCancelled = true
        }
    }

    override fun getPossibleRewards(player: Player): HashMap<String, Reward> {
        val finalRewards = HashMap<String, Reward>()
        for ((id, pair) in rewards) {
            val reward = pair.first
            if (!reward.requirements.checkRequirements(player)) continue

            var meetsRequirements = true
            for ((type, limit) in reward.globalLimits) {
                if (HistoryHandler.history("pouch:$identifier", id, type) >= limit) {
                    meetsRequirements = false
                    break
                }
            }
            if (!meetsRequirements) continue
            for ((type, limit) in reward.perPlayerLimits) {
                if (HistoryHandler.history("pouch:$identifier", id, type, player) >= limit) {
                    meetsRequirements = false
                    break
                }
            }
            if (!meetsRequirements) continue

            finalRewards[id] = reward
        }

        return finalRewards
    }

    open fun open(player: Player) {

    }

    abstract fun canBeOpened(player: Player): Boolean

    fun giveItem(amount: Int, vararg players: Player) {
        val itemStack = getItem(amount)

        for (player in players) {
            val iS = itemStack.clone()
            for ((_, item) in player.inventory.addItem(iS)) {
                player.world.dropItem(player.location, item)
            }
        }
    }

    fun getItem(amount: Int): ItemStack {
        val itemStack = item.getItem()
        itemStack.amount = amount

        return itemStack
    }
}