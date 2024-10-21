package gg.aquatic.aquaticcrates.api.crate

import gg.aquatic.aquaticseries.lib.item2.AquaticItem
import gg.aquatic.waves.item.ItemHandler
import gg.aquatic.waves.registry.register
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

abstract class Key(
    val crate: Crate,
    val item: AquaticItem
) {

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
            interactHandler.handleInteract(it.player, it.isLeftClick)
            it.isCancelled = true
        }
    }

}