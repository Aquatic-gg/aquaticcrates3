package gg.aquatic.aquaticcrates.api.pouch

import gg.aquatic.aquaticcrates.api.interaction.InteractHandler

abstract class PouchInteractHandler: InteractHandler() {

    abstract val pouch: Pouch

}