package gg.aquatic.aquaticcrates.plugin.editor.category.key

import gg.aquatic.aquaticcrates.plugin.editor.EditorCategory
import gg.aquatic.aquaticcrates.plugin.editor.category.item.ItemEditorCategory
import gg.aquatic.aquaticcrates.plugin.editor.data.CrateModel
import gg.aquatic.aquaticcrates.plugin.editor.item.CategoryEditorItem
import gg.aquatic.aquaticcrates.plugin.editor.item.ToggleEditorItem

class KeyEditorCategory(
    val crateModel: CrateModel
) : EditorCategory(
    crateModel.key.item.aquaticItem.getItem()
) {
    override fun refresh(): EditorCategory {
        return KeyEditorCategory(crateModel)
    }

    init {
        items += "item" to CategoryEditorItem(ItemEditorCategory(crateModel.key.item))
        items += "must-be-held" to ToggleEditorItem(
            crateModel.key.mustBeHeld,
            "Must be Held",
            listOf(
                "If the key must be held in hand in order to open the crate"
            )
        ) { b ->
            crateModel.key.mustBeHeld = b
        }
    }

}