package gg.aquatic.aquaticcrates.plugin.animation.idle

import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate
import gg.aquatic.waves.scenario.Scenario
import gg.aquatic.waves.scenario.ScenarioProp
import gg.aquatic.waves.util.audience.AquaticAudience
import gg.aquatic.waves.util.collection.executeActions
import net.kyori.adventure.key.Key
import java.util.concurrent.ConcurrentHashMap

class IdleAnimationImpl(
    val crate: SpawnedCrate,
    val settings: IdleAnimationSettings
) : Scenario() {
    override val baseLocation = crate.location
    override val audience: AquaticAudience = crate.audience
    override val props: MutableMap<Key, ScenarioProp> = ConcurrentHashMap()

    override fun onTick() {
        tickProps()
        settings.animationTasks[tick]?.executeActions(this) { a, str ->
            a.updatePlaceholders(str)
        }

        if (tick > settings.length) {
            if (settings.isLoop) {
                tick = 0
                return
            }
            props.values.forEach { it.onEnd() }
            (crate.crate as OpenableCrate).animationManager.playNewIdleAnimation(crate)
        }
    }

    override val extraPlaceholders: MutableMap<Key, (String) -> String> = ConcurrentHashMap()

    override fun updatePlaceholders(str: String): String {
        var finalString = str

        for ((_, extraPlaceholder) in extraPlaceholders) {
            finalString = extraPlaceholder(finalString)
        }

        return finalString
    }
}