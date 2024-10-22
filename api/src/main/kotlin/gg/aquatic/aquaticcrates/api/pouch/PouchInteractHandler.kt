package gg.aquatic.aquaticcrates.api.pouch

import gg.aquatic.aquaticcrates.api.util.InteractHandler

abstract class PouchInteractHandler: InteractHandler() {

    abstract val pouch: Pouch

}