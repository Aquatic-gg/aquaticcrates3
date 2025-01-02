package gg.aquatic.aquaticcrates.api.animation

import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.waves.util.audience.AquaticAudience
import gg.aquatic.waves.util.updatePAPIPlaceholders
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

    val extraPlaceholders = ArrayList<(String) -> String>()

    fun updatePlaceholders(str: String): String {
        var finalString = str.updatePAPIPlaceholders(player).replace("%player%", player.name)

        for ((i, reward) in rewards.withIndex()) {
            finalString = finalString
                .replace("%random-amount:$i%", reward.randomAmount.toString())
                .replace("%reward-name:$i%", reward.reward.displayName)
                .replace("%reward-id:$i%", reward.reward.id)
                .replace("%reward-chance:$i%", reward.reward.chance.toString())
        }

        for (extraPlaceholder in extraPlaceholders) {
            finalString = extraPlaceholder(finalString)
        }

        return finalString
    }
}