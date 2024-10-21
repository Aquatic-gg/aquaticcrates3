package gg.aquatic.aquaticcrates.api.pouche

import gg.aquatic.aquaticcrates.api.util.InteractHandler

abstract class PouchInteractHandler: InteractHandler() {

    abstract val pouch: RewardPouch

}