package gg.aquatic.aquaticcrates.api.crate

abstract class Crate {
    abstract val identifier: String
    abstract val displayName: String
    abstract val hologram: List<String>
    abstract val hologramOffset: Double
    abstract val interactHandler: InteractHandler

}