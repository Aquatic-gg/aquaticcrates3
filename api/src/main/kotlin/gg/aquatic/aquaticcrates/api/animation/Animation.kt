package gg.aquatic.aquaticcrates.api.animation

import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.waves.util.audience.AquaticAudience
import gg.aquatic.waves.util.updatePAPIPlaceholders
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap

abstract class Animation {

    abstract val baseLocation: Location
    abstract val audience: AquaticAudience
    abstract val props: MutableMap<String, AnimationProp>

    var tick: Int = 0
        protected set

    abstract fun tick()

    val extraPlaceholders = ConcurrentHashMap<String,(String) -> String>()

    abstract fun updatePlaceholders(str: String): String
}