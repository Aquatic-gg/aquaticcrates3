package gg.aquatic.aquaticcrates.plugin.editor.category

import gg.aquatic.aquaticcrates.api.util.createItem
import gg.aquatic.aquaticcrates.plugin.editor.EditorCategory
import gg.aquatic.aquaticcrates.plugin.editor.category.key.KeyEditorCategory
import gg.aquatic.aquaticcrates.plugin.editor.data.CrateModel
import gg.aquatic.aquaticcrates.plugin.editor.item.CategoryEditorItem
import gg.aquatic.aquaticcrates.plugin.editor.item.InputEditorItem
import gg.aquatic.waves.input.impl.ChatInput
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class MainEditorCategory(
    val crateModel: CrateModel
) : EditorCategory(
    ItemStack.of(Material.STONE)
) {
    override fun refresh(): EditorCategory {
        return MainEditorCategory(crateModel)
    }

    init {
        val keyCategory = KeyEditorCategory(crateModel)
        items += "key" to CategoryEditorItem(keyCategory)
        items += "display-name" to InputEditorItem(
            createItem(Material.NAME_TAG) {
                displayName = "Crate Display Name"
                lore = listOf(
                    "The display name of crate",
                    "Current display name: ${crateModel.displayName}"
                )
            },
            ChatInput.createHandle(listOf("cancel")),
            { str ->
                crateModel.displayName = str
            },
            listOf(
                "Enter the Display Name for the Crate...",
                "Type 'cancel' to cancel the input!"
            )
        )
    }
}