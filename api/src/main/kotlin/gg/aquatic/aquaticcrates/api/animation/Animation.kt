package gg.aquatic.aquaticcrates.api.animation

import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.waves.util.audience.AquaticAudience
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

    val textUpdaters = ArrayList<(Animation, String) -> String>().apply {
        add { animation, str -> str.replace("%player%", animation.player.name) }
        add { animation, str ->

            var finalString = str
            for ((i, reward) in rewards.withIndex()) {
                finalString = finalString
                    .replace("%random-amount:$i%", reward.randomAmount.toString())
                    .replace("%reward-name:$i%", reward.reward.displayName)
                    .replace("%reward-id:$i%", reward.reward.id)
                    .replace("%reward-chance:$i%", reward.reward.chance.toString())
            }
            finalString

        }
    }

    fun updatePlaceholders(str: String): String {
        var finalString = str.replace("%player%", player.name)

        for (textUpdater in textUpdaters) {
            finalString = textUpdater(this, finalString)
        }

        return finalString
    }
}