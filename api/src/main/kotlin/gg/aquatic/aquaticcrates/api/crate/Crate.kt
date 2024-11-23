package gg.aquatic.aquaticcrates.api.crate

import gg.aquatic.aquaticcrates.api.hologram.HologramSettings
import gg.aquatic.aquaticseries.lib.interactable2.AbstractInteractable
import gg.aquatic.waves.interactable.settings.InteractableSettings

abstract class Crate {
    abstract val identifier: String
    abstract val displayName: String
    abstract val hologramSettings: HologramSettings
    abstract val interactHandler: CrateInteractHandler
    abstract val interactables: List<InteractableSettings>
}