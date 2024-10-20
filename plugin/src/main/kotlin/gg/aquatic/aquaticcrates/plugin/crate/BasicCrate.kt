package gg.aquatic.aquaticcrates.plugin.crate

import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.crate.InteractHandler
import gg.aquatic.aquaticcrates.api.hologram.HologramSettings
import gg.aquatic.aquaticseries.lib.interactable2.AbstractInteractable

class BasicCrate(
    override val identifier: String,
    override val displayName: String,
    override val hologramSettings: HologramSettings,
    override val interactHandler: InteractHandler,
    override val interactable: AbstractInteractable<*>
) : Crate() {
}