package gg.aquatic.aquaticcrates.plugin.crate.key

import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.crate.Key
import gg.aquatic.aquaticcrates.api.crate.KeyInteractHandler
import gg.aquatic.waves.item.AquaticItem

class KeyImpl(
    crate: Crate,
    item: AquaticItem,
    override val mustBeHeld: Boolean,
    interactHandler: (KeyImpl) -> KeyInteractHandler
) : Key(crate, item) {

    override val interactHandler = interactHandler(this)
}