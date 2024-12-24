package gg.aquatic.aquaticcrates.api.hologram

import gg.aquatic.waves.util.audience.AquaticAudience
import org.bukkit.Location
import org.bukkit.entity.Player

abstract class Hologram(
    var location: Location
) {

    abstract fun move(location: Location)

    abstract fun despawn()

    abstract fun spawn(audience: AquaticAudience, textUpdater: (Player, String) -> String)

    abstract fun update(textUpdater: (Player,String) -> String)


}