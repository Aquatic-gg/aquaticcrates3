package gg.aquatic.aquaticcrates.plugin.animation.idle

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate
import gg.aquatic.aquaticcrates.plugin.animation.idle.settings.IdleAnimationSettings
import gg.aquatic.waves.util.audience.AquaticAudience
import gg.aquatic.waves.util.collection.executeActions
import org.bukkit.Location
import java.util.concurrent.ConcurrentHashMap

class IdleAnimationImpl(
    val crate: SpawnedCrate,
    override val baseLocation: Location,
    val settings: IdleAnimationSettings
) : Animation() {
    override val audience: AquaticAudience = crate.audience
    override val props: MutableMap<String, AnimationProp> = ConcurrentHashMap()

    override fun tick() {
        for ((_, prop) in props) {
            prop.tick()
        }
        settings.animationTasks[tick]?.executeActions(this) { a, str ->
            a.updatePlaceholders(str)
        }

        tick++

        if (tick > settings.length) {
            if (settings.isLoop) {
                tick = 0
                return
            }
            // START NEW
        }
    }

    override fun updatePlaceholders(str: String): String {
        var finalString = str

        for ((_, extraPlaceholder) in extraPlaceholders) {
            finalString = extraPlaceholder(finalString)
        }

        return finalString
    }
}