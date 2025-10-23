package gg.aquatic.aquaticcrates.plugin.editor.item

import gg.aquatic.aquaticcrates.plugin.editor.menu.EditorMenu
import gg.aquatic.waves.input.InputHandle
import gg.aquatic.waves.inventory.event.AsyncPacketInventoryInteractEvent
import gg.aquatic.waves.util.task.BukkitCtx
import gg.aquatic.waves.util.toMMComponent
import org.bukkit.inventory.ItemStack

class InputEditorItem(
    override val itemStack: ItemStack,
    val inputHandle: InputHandle,
    val dataApplier: (String) -> Unit,
    val chatMessage: Collection<String> = listOf(),
    val validator: (String) -> Boolean = { true },
) : EditorItem {

    override fun execute(event: AsyncPacketInventoryInteractEvent) {
        val player = event.viewer.player
        val menu = event.inventory as EditorMenu

        BukkitCtx {
            player.closeInventory()

            for (string in chatMessage) {
                player.sendMessage(string.toMMComponent())
            }
            inputHandle.await(player).thenAccept { input ->
                if (input == null) {
                    menu.open()
                    return@thenAccept
                }
                if (!validator(input)) {
                    return@thenAccept
                }

                dataApplier(input)
                val newMenu = EditorMenu(menu.crateModel, player, menu.category.refresh(), menu.previousMenu)
                newMenu.open()
            }
        }
    }

    companion object {
        fun builder(itemStack: ItemStack, inputHandle: InputHandle, builder: Builder.() -> Unit) =
            Builder(itemStack, inputHandle).apply(builder).build()
    }

    class Builder(val itemStack: ItemStack, val inputHandle: InputHandle) {
        var validator: (String) -> Boolean = { true }
        var chatMessage: Collection<String> = listOf()
        var dataApplier: (String) -> Unit = {}

        fun build(): InputEditorItem {
            return InputEditorItem(itemStack, inputHandle, dataApplier, chatMessage, validator)
        }
    }

}