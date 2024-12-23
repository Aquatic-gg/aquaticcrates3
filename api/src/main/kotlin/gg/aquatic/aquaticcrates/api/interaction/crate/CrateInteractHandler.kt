package gg.aquatic.aquaticcrates.api.interaction.crate

import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.interaction.CrateInteractAction
import gg.aquatic.aquaticcrates.api.interaction.InteractHandler
import gg.aquatic.waves.item.AquaticItemInteractEvent
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import java.util.EnumMap

abstract class CrateInteractHandler : InteractHandler() {

    abstract val clickActions: EnumMap<AquaticItemInteractEvent.InteractType, ConfiguredExecutableObject<CrateInteractAction,Unit>>
    abstract val crate: Crate
}