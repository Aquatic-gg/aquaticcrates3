package gg.aquatic.aquaticcrates.api.animation

import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.aquaticseries.lib.audience.AquaticAudience
import org.bukkit.Location
import org.bukkit.entity.Player

abstract class Animation {

    abstract val baseLocation: Location
    abstract val player: Player
    abstract val audience: AquaticAudience
    abstract val rewards: MutableList<RolledReward>
    abstract val props: MutableMap<String, AnimationProp>

    var tick: Int = 0
        protected set
    abstract fun tick()

    abstract fun tickPreOpen()
    abstract fun tickOpening()
    abstract fun tickPostOpen()

}