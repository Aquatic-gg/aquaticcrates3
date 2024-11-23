package gg.aquatic.aquaticcrates.api.interaction.crate

import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.interaction.InteractHandler
import gg.aquatic.waves.item.AquaticItemInteractEvent
import java.util.EnumMap

abstract class CrateInteractHandler: InteractHandler() {

    abstract val clickActions: EnumMap<AquaticItemInteractEvent.InteractType, CrateInteractionAction>
    abstract val crate: Crate
}