package gg.aquatic.aquaticcrates.api.crate

import gg.aquatic.aquaticcrates.api.AbstractCratesPlugin
import gg.aquatic.aquaticseries.lib.item2.AquaticItem
import gg.aquatic.waves.registry.NAMESPACE_KEY
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

abstract class Key {

    companion object {
        val NAMESPACED_KEY = NamespacedKey(AbstractCratesPlugin.INSTANCE, "aquaticrates_key_identifier")
        fun get(id: String): Key? {
            val crate = CrateHandler.crates[id] as? OpenableCrate ?: return null
            return crate.key
        }
        fun get(itemStack: ItemStack): Key? {
            val meta = itemStack.itemMeta ?: return null
            val id = meta.persistentDataContainer.get(NAMESPACE_KEY, PersistentDataType.STRING) ?: return null
            return get(
                id
            )
        }
    }

    abstract val crate: Crate
    abstract val item: AquaticItem
    abstract val requiresCrateToOpen: Boolean
    abstract val mustBeHeld: Boolean


}