package gg.aquatic.aquaticcrates.api.crate

import gg.aquatic.aquaticcrates.api.util.InteractHandler

abstract class KeyInteractHandler: InteractHandler() {

    abstract val requiresCrateToOpen: Boolean
    abstract val key: Key

}