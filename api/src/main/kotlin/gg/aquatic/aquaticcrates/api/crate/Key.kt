package gg.aquatic.aquaticcrates.api.crate

import gg.aquatic.aquaticcrates.api.interaction.InteractHandler
import gg.aquatic.aquaticcrates.api.interaction.key.KeyInteractHandler
import gg.aquatic.aquaticcrates.api.util.ItemBased
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.item.ItemHandler
import gg.aquatic.waves.registry.register
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
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
            if (!namespace.equals("cratekey", true)) return null
            return get(
                id
            )
        }
    }

    abstract val mustBeHeld: Boolean
    abstract val interactHandler: KeyInteractHandler

    init {
        item.register("aquaticcrates", "cratekey:${crate.identifier}") {
            val originalEvent = it.originalEvent

            val location = if (originalEvent is PlayerInteractEvent) {
                originalEvent.clickedBlock?.location ?: originalEvent.player.location
            } else it.player.location

            if (interactHandler.handleInteract(it.player, it.interactType, location, null)) {
                it.isCancelled = true
                return@register
            }
        }
    }

}