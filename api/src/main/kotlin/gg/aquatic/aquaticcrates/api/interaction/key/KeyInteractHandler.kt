package gg.aquatic.aquaticcrates.api.interaction.key

import gg.aquatic.aquaticcrates.api.crate.Key
import gg.aquatic.aquaticcrates.api.interaction.CrateInteractAction
import gg.aquatic.aquaticcrates.api.interaction.InteractHandler
import gg.aquatic.waves.item.AquaticItemInteractEvent
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import java.util.EnumMap

abstract class KeyInteractHandler: InteractHandler() {

    abstract val clickActions: EnumMap<AquaticItemInteractEvent.InteractType,ConfiguredExecutableObject<CrateInteractAction,Unit>>
    abstract val key: Key

}