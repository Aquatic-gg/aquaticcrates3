package gg.aquatic.aquaticcrates.plugin.editor.data

import gg.aquatic.aquaticcrates.plugin.crate.BasicCrate
import gg.aquatic.aquaticcrates.plugin.editor.data.key.KeyModel

data class CrateModel(
    var id: String,
    var displayName: String,
    var key: KeyModel
) {

    companion object {
        fun of(crate: BasicCrate): CrateModel {
            return CrateModel(
                crate.identifier,
                crate.displayName,
                KeyModel.of(crate)
            )
        }
    }

}