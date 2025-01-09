package gg.aquatic.aquaticcrates.plugin.animation.action.inventory

import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.plugin.animation.prop.inventory.InventoryAnimationProp
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.menu.MenuSerializer
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AbstractObjectArgumentSerializer
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.getSectionList
import gg.aquatic.waves.util.item.loadFromYml
import org.bukkit.configuration.ConfigurationSection

class OpenInventoryAction : AbstractAction<PlayerBoundAnimation>() {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("size", 9, true),
        PrimitiveObjectArgument("title", "Inventory", true),
        ItemsArgument("items", mapOf(), true)
    )

    override fun execute(
        binder: PlayerBoundAnimation,
        args: Map<String, Any?>,
        textUpdater: (PlayerBoundAnimation, String) -> String
    ) {
        val size = args["size"] as? Int ?: 9
        val title = args["title"] as? String ?: "Inventory"
        val items = args["items"] as? Map<Int, AquaticItem> ?: mapOf()
        val prop = InventoryAnimationProp(
            binder,
            title, size, items
        )
        val animationMenu = binder.props["inventory"] as? InventoryAnimationProp
        animationMenu?.menu?.close()

        binder.props["inventory"] = prop
        prop.menu.open()
    }

    class ItemsArgument(
        id: String,
        defaultValue: Map<Int, AquaticItem>?, required: Boolean
    ) : AquaticObjectArgument<Map<Int, AquaticItem>>(id, defaultValue, required) {
        override val serializer: AbstractObjectArgumentSerializer<Map<Int, AquaticItem>?> = Companion

        override fun load(section: ConfigurationSection): Map<Int, AquaticItem> {
            return Companion.load(section, id)
        }

        companion object : AbstractObjectArgumentSerializer<Map<Int, AquaticItem>?>() {
            override fun load(section: ConfigurationSection, id: String): Map<Int, AquaticItem> {
                val map = mutableMapOf<Int, AquaticItem>()
                for (configurationSection in section.getSectionList(id)) {
                    val slots = MenuSerializer.loadSlotSelection(configurationSection.getStringList("slots"))
                    val item = AquaticItem.loadFromYml(configurationSection) ?: continue

                    for (slot in slots.slots) {
                        map[slot] = item
                    }
                }
                return map
            }
        }

    }
}