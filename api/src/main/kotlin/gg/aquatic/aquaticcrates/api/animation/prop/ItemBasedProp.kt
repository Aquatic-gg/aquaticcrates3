package gg.aquatic.aquaticcrates.api.animation.prop

import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.scenario.ScenarioProp

interface ItemBasedProp: ScenarioProp {

    fun item(): AquaticItem?

}