package gg.aquatic.aquaticcrates.plugin.animation.action.inventory

import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.api.util.animationitem.ArgumentItem
import gg.aquatic.aquaticcrates.plugin.animation.prop.inventory.InventoryAnimationProp
import gg.aquatic.waves.menu.MenuSerializer
import gg.aquatic.waves.util.argument.AbstractObjectArgumentSerializer
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.generic.Action
import gg.aquatic.waves.util.getSectionList
import org.bukkit.configuration.ConfigurationSection

class OpenInventoryAction : Action<PlayerBoundAnimation> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("size", 9, true),
        PrimitiveObjectArgument("title", "Inventory", true),
        ItemsArgument("items", mapOf(), true)
    )

    override fun execute(
        binder: PlayerBoundAnimation,
        args: ObjectArguments,
        textUpdater: (PlayerBoundAnimation, String) -> String
    ) {
        val size = args.int("size") { textUpdater(binder, it) } ?: 9
        val title = args.string("title") { textUpdater(binder, it) } ?: "Inventory"
        val items = args.typed<Map<Int, ArgumentItem>>("items") { textUpdater(binder, it) } ?: mapOf()
        val prop = InventoryAnimationProp(
            binder,
            title, size, items.mapValues { it.value.getActualItem(binder) }
        )
        val animationMenu = binder.props["inventory"] as? InventoryAnimationProp
        animationMenu?.menu?.close()

        binder.props["inventory"] = prop
        prop.menu.open()
    }

    class ItemsArgument(
        id: String,
        defaultValue: Map<Int, ArgumentItem>?, required: Boolean
    ) : AquaticObjectArgument<Map<Int, ArgumentItem>>(id, defaultValue, required) {
        override val serializer: AbstractObjectArgumentSerializer<Map<Int, ArgumentItem>?> = Companion

        override fun load(section: ConfigurationSection): Map<Int, ArgumentItem> {
            return Companion.load(section, id)
        }

        companion object : AbstractObjectArgumentSerializer<Map<Int, ArgumentItem>?>() {
            override fun load(section: ConfigurationSection, id: String): Map<Int, ArgumentItem> {
                val map = mutableMapOf<Int, ArgumentItem>()
                for (configurationSection in section.getSectionList(id)) {
                    val slots = MenuSerializer.loadSlotSelection(configurationSection.getStringList("slots"))
                    val item = ArgumentItem.loadFromYml(configurationSection)

                    for (slot in slots.slots) {
                        map[slot] = item
                    }
                }
                return map
            }
        }

    }
}