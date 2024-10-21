package gg.aquatic.aquaticcrates.api.crate

import gg.aquatic.aquaticcrates.api.util.InteractHandler

abstract class CrateInteractHandler: InteractHandler() {

    abstract val crate: Crate

}