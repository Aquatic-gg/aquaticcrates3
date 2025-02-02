package gg.aquatic.aquaticcrates.plugin.animation.fail

import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.waves.util.audience.AquaticAudience
import gg.aquatic.waves.util.updatePAPIPlaceholders
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap

class FailAnimation(
    override val baseLocation: Location,
    override val player: Player,
    override val audience: AquaticAudience,
) : PlayerBoundAnimation() {

    override val props: MutableMap<String, AnimationProp> = ConcurrentHashMap()
    override fun tick() {

    }

    override fun updatePlaceholders(str: String): String {
        var finalString = str.replace("%player%", player.name).updatePAPIPlaceholders(player)

        for ((_, extraPlaceholder) in extraPlaceholders) {
            finalString = extraPlaceholder(finalString)
        }

        return finalString
    }
}