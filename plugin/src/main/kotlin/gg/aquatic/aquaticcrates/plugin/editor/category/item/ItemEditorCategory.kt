package gg.aquatic.aquaticcrates.plugin.editor.category.item

import gg.aquatic.aquaticcrates.api.util.createItem
import gg.aquatic.aquaticcrates.plugin.editor.EditorCategory
import gg.aquatic.aquaticcrates.plugin.editor.category.collection.CollectionEditorCategory
import gg.aquatic.aquaticcrates.plugin.editor.data.ItemModel
import gg.aquatic.aquaticcrates.plugin.editor.item.CategoryEditorItem
import gg.aquatic.aquaticcrates.plugin.editor.item.InputEditorItem
import gg.aquatic.waves.input.impl.ChatInput
import org.bukkit.Material

class ItemEditorCategory(val itemModel: ItemModel) : EditorCategory(itemModel.aquaticItem.getItem()) {
    override fun refresh(): EditorCategory {
        return ItemEditorCategory(itemModel)
    }

    init {

        this.items += "material" to InputEditorItem(
            itemModel.aquaticItem.getItem(),
            ChatInput.createHandle(listOf("cancel")),
            { str ->
                itemModel.material = str
            },
            listOf(
                "Enter the Material for the Item...",
                "Type 'cancel' to cancel the input!"
            ),
        )
        this.items += "display-name" to InputEditorItem(
            createItem(Material.NAME_TAG) {
                displayName = "Display Name"
            },
            ChatInput.createHandle(listOf("cancel")),
            { str ->
                itemModel.options
                //itemModel.displayName = str
            },
            listOf(
                "Enter the Display Name for the Item...",
                "Type 'cancel' to cancel the input!"
            ),

            )
        /*
        this.items += "lore" to CategoryEditorItem(
            CollectionEditorCategory(
                createItem(Material.BOOK) {
                    displayName = "Lore"
                    lore = listOf(
                        "The lore of the item",
                        "Current lore: ${itemModel.lore.joinToString("\n")}"
                    )
                },
                itemModel.lore,
                { index, line ->
                    "$index" to CollectionEditorCategory.CollectionEditorItem(
                        index, InputEditorItem(
                            createItem(Material.PAPER) {
                                displayName = line
                                lore = listOf(
                                    "Line: #$index",
                                    "",
                                    "<gray>[Left-Click] to change the line!",
                                    "<gray>[Shift-Right-Click] to remove the line!"
                                )
                            },
                            ChatInput.createHandle(listOf("cancel")),
                            { str ->
                                itemModel.lore[index] = str
                            },
                            listOf(
                                "Enter the Lore Line #$index for the Item...",
                                "Type 'cancel' to cancel the input!"
                            )
                        )
                    )
                },
                InputEditorItem(
                    createItem(Material.EMERALD) {
                        displayName = "Add Line"
                        lore = listOf(
                            "",
                            "[Left-Click] to add a new line!"
                        )
                    },
                    ChatInput.createHandle(listOf("cancel")),
                    { str ->
                        val lore = itemModel.lore
                        lore.add(str)
                        itemModel.lore = lore
                    },
                    listOf(
                        "Enter the Lore Line for the Item...",
                        "Type 'cancel' to cancel the input!"
                    ),
                )
            )
        )
        this.items += "amount" to InputEditorItem(
            createItem(Material.MINECART) {
                displayName = "Amount"
                lore = listOf(
                    "The amount of the item",
                    "Current amount: ${itemModel.amount}"
                )
            },
            ChatInput.createHandle(listOf("cancel")),
            { str ->
                itemModel.amount = str.toIntOrNull() ?: 1
            },
            listOf(
                "Enter the Amount for the Item...",
                "Type 'cancel' to cancel the input!"
            ),
            { str ->
                str.toIntOrNull() != null && str.toInt() > 0
            }
        )

         */
    }

}