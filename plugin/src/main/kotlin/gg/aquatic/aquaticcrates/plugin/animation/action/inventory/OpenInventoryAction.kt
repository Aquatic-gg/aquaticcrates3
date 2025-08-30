package gg.aquatic.aquaticcrates.plugin.animation.action.inventory

import gg.aquatic.aquaticcrates.api.util.animationitem.ArgumentItem
import gg.aquatic.aquaticcrates.plugin.animation.prop.inventory.InventoryAnimationProp
import gg.aquatic.waves.menu.MenuSerializer
import gg.aquatic.waves.scenario.PlayerScenario
import gg.aquatic.waves.util.action.RegisterAction
import gg.aquatic.waves.util.argument.AbstractObjectArgumentSerializer
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.generic.Action
import gg.aquatic.waves.util.getSectionList
import net.kyori.adventure.key.Key
import org.bukkit.configuration.ConfigurationSection

@RegisterAction("open-inventory")
class OpenInventoryAction : Action<PlayerScenario> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("size", 9, true),
        PrimitiveObjectArgument("title", "Inventory", true),
        ItemsArgument("items", mapOf(), true)
    )

    override fun execute(
        binder: PlayerScenario,
        args: ObjectArguments,
        textUpdater: (PlayerScenario, String) -> String
    ) {
        val size = args.int("size") { textUpdater(binder, it) } ?: 9
        val title = args.string("title") { textUpdater(binder, it) } ?: "Inventory"
        val items = args.typed<Map<Int, ArgumentItem>>("items") { textUpdater(binder, it) } ?: mapOf()
        val prop = InventoryAnimationProp(
            binder,
            title, size, items.mapValues { it.value.getActualItem(binder) }
        )
        val animationMenu = binder.props[Key.key("inventory")] as? InventoryAnimationProp
        animationMenu?.menu?.close()

        binder.props[Key.key("inventory")] = prop
        prop.menu.open()
    }

    class ItemsArgument(
        id: String,
        defaultValue: Map<Int, ArgumentItem>?, required: Boolean, aliases: Collection<String> = listOf()
    ) : AquaticObjectArgument<Map<Int, ArgumentItem>>(id, defaultValue, required, aliases) {
        override val serializer: AbstractObjectArgumentSerializer<Map<Int, ArgumentItem>?> = Companion

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