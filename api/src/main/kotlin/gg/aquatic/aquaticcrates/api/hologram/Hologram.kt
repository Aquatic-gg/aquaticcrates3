package gg.aquatic.aquaticcrates.api.hologram

import gg.aquatic.aquaticseries.lib.audience.AquaticAudience
import org.bukkit.Location
import java.util.function.Consumer

abstract class Hologram(
    var location: Location
) {

    abstract fun move(location: Location)

    abstract fun despawn()

    abstract fun spawn(audience: AquaticAudience, consumer: Consumer<List<String>>)

    abstract fun update(consumer: Consumer<List<String>>)


}