package gg.aquatic.aquaticcrates.plugin.editor.item

import gg.aquatic.aquaticcrates.plugin.editor.menu.EditorMenu
import gg.aquatic.waves.inventory.event.AsyncPacketInventoryInteractEvent
import gg.aquatic.waves.menu.component.Button
import org.bukkit.inventory.ItemStack

interface EditorItem {

    val itemStack: ItemStack
    fun execute(event: AsyncPacketInventoryInteractEvent)

    fun createButton(id: String, slot: Int, updater: (String, EditorMenu) -> String = { str, _ -> str }): Button {
        val button = Button(
            id,
            itemStack,
            listOf(slot),
            1,
            100,
            null,
            { true },
            onClick = ::execute,
            textUpdater = { str, menu -> updater(str, menu as EditorMenu) }
        )
        return button
    }
}