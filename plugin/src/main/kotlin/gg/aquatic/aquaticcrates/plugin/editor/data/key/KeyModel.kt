package gg.aquatic.aquaticcrates.plugin.editor.data.key

import gg.aquatic.aquaticcrates.plugin.crate.BasicCrate
import gg.aquatic.aquaticcrates.plugin.editor.data.ItemModel
import gg.aquatic.aquaticcrates.plugin.editor.data.interact.CrateInteractActionModel
import gg.aquatic.waves.item.AquaticItemInteractEvent

data class KeyModel(
    var item: ItemModel,
    var mustBeHeld: Boolean,
    var clickActions: HashMap<AquaticItemInteractEvent.InteractType, CrateInteractActionModel>
    ) {

    companion object {
        fun of(crate: BasicCrate): KeyModel {
            return KeyModel(
                ItemModel.of(crate.key.item),
                crate.key.mustBeHeld,
                hashMapOf()
            )
        }
    }
}