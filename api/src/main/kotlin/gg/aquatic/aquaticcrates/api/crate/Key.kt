package gg.aquatic.aquaticcrates.api.crate

import gg.aquatic.aquaticcrates.api.interaction.key.KeyInteractHandler
import gg.aquatic.aquaticcrates.api.util.ItemBased
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.item.AquaticItemInteractEvent
import gg.aquatic.waves.item.ItemHandler
import gg.aquatic.waves.registry.register
import gg.aquatic.waves.registry.setInteractionHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

abstract class Key(
    val crate: Crate,
    final override val item: AquaticItem
) : ItemBased {

    companion object {
        fun get(id: String): Key? {
            val crate = CrateHandler.crates[id] as? OpenableCrate ?: return null
            return crate.key
        }

        fun get(itemStack: ItemStack): Key? {
            val meta = itemStack.itemMeta ?: return null
            val pair =
                meta.persistentDataContainer.get(ItemHandler.NAMESPACE_KEY, PersistentDataType.STRING) ?: return null
            val id = pair.substringAfter(":")
            val namespace = pair.substringBefore(":")
            if (!namespace.equals("aquaticcrates-key", true)) return null
            return get(
                id
            )
        }
    }

    abstract val mustBeHeld: Boolean
    abstract val interactHandler: KeyInteractHandler

    init {
        val consumer: (AquaticItemInteractEvent) -> Unit = {
            val originalEvent = it.originalEvent

            val location = if (originalEvent is PlayerInteractEvent) {
                originalEvent.clickedBlock?.location ?: originalEvent.player.location
            } else it.player.location

            interactHandler.handleInteract(it.player, it.interactType, location, null)
            if (originalEvent !is InventoryClickEvent) {
                it.isCancelled = true
            }
        }
        if (!item.register("aquaticcrates-key", crate.identifier, consumer)) {
            item.setInteractionHandler(consumer)
        }
    }

}