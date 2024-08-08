package gg.aquatic.aquaticcrates.api.fake

import gg.aquatic.aquaticcrates.api.AbstractAudience
import org.bukkit.Location

abstract class FakeObject {

    abstract val location: Location
    abstract val audience: AbstractAudience

    var spawned = false

    abstract fun spawn()
    abstract fun despawn()

}