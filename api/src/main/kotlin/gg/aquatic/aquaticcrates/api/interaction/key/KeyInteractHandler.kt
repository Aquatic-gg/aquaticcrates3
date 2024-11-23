package gg.aquatic.aquaticcrates.api.interaction.key

import gg.aquatic.aquaticcrates.api.crate.Key
import gg.aquatic.aquaticcrates.api.interaction.InteractHandler
import gg.aquatic.aquaticcrates.api.interaction.crate.CrateInteractionAction
import gg.aquatic.waves.item.AquaticItemInteractEvent
import java.util.EnumMap

abstract class KeyInteractHandler: InteractHandler() {

    abstract val clickActions: EnumMap<AquaticItemInteractEvent.InteractType,CrateInteractionAction>
    abstract val requiresCrateToOpen: Boolean
    abstract val key: Key

}