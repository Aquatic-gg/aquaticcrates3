package gg.aquatic.aquaticcrates.api.interaction.key

import gg.aquatic.aquaticcrates.api.crate.Key
import gg.aquatic.aquaticcrates.api.interaction.CrateInteractAction
import gg.aquatic.aquaticcrates.api.interaction.InteractHandler
import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import gg.aquatic.waves.item.AquaticItemInteractEvent
import java.util.EnumMap

abstract class KeyInteractHandler: InteractHandler() {

    abstract val clickActions: EnumMap<AquaticItemInteractEvent.InteractType,ConfiguredAction<CrateInteractAction>>
    abstract val requiresCrateToOpen: Boolean
    abstract val key: Key

}