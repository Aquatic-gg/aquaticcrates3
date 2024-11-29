package gg.aquatic.aquaticcrates.api.pouch

import gg.aquatic.aquaticcrates.api.animation.pouch.PouchAnimationManager
import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import gg.aquatic.aquaticcrates.api.milestone.MilestoneManager
import gg.aquatic.aquaticcrates.api.openprice.OpenPriceGroup
import gg.aquatic.aquaticcrates.api.player.HistoryHandler
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.aquaticcrates.api.util.ItemBased
import gg.aquatic.aquaticcrates.api.util.Rewardable
import gg.aquatic.aquaticseries.lib.requirement.ConfiguredRequirement
import gg.aquatic.aquaticseries.lib.util.checkRequirements
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.item.ItemHandler
import gg.aquatic.waves.registry.isAquaticItem
import gg.aquatic.waves.registry.register
import gg.aquatic.waves.registry.registryId
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

abstract class Pouch(
    val identifier: String,
    final override val item: AquaticItem
): Rewardable, ItemBased {

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
    abstract val milestoneManager: MilestoneManager

    init {
        item.register("aquaticcrates", "pouch:$identifier") {
            val originalEvent = it.originalEvent
            val location = if (originalEvent is PlayerInteractEvent) {
                originalEvent.clickedBlock?.location ?: originalEvent.player.location
            } else return@register
            interactHandler.handleInteract(it.player, it.interactType, location, null)
            it.isCancelled = true
        }
    }

    /*
    override fun getPossibleRewards(player: Player): HashMap<String, Reward> {
        val finalRewards = HashMap<String, Reward>()
        for ((id, reward) in rewards) {
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
     */

    open fun tryOpen(player: Player, interactionLocation: Location) {
        if (!canBeOpened(player)) return

        val holdingItem = player.inventory.itemInMainHand
        val aquaticItem = holdingItem.isAquaticItem() ?: return
        if (aquaticItem.registryId() != item.registryId()) return

        holdingItem.amount -= 1

        open(player, interactionLocation, false)
    }

    open fun open(player: Player, interactionLocation: Location, force: Boolean) {
        val rolledRewards = generateRewards(player)
        if (force) {
            for (rolledReward in rolledRewards) {
                rolledReward.give(player, false)
            }
            return
        }
        val animation = animationManager.animationSettings.create(player, animationManager, interactionLocation, rolledRewards)
        animationManager.playingAnimations.getOrPut(player.uniqueId) { arrayListOf() } += animation
    }

    abstract fun generateRewards(player: Player): MutableList<RolledReward>

    abstract fun canBeOpened(player: Player): Boolean
}