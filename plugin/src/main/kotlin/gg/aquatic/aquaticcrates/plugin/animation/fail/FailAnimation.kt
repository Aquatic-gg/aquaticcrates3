package gg.aquatic.aquaticcrates.plugin.animation.fail

import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate
import gg.aquatic.waves.scenario.PlayerScenario
import gg.aquatic.waves.scenario.ScenarioProp
import gg.aquatic.waves.util.audience.FilterAudience
import gg.aquatic.waves.util.collection.executeActions
import gg.aquatic.waves.util.updatePAPIPlaceholders
import net.kyori.adventure.key.Key
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

class FailAnimation(
    val settings: FailAnimationSettings,
    override val player: Player,
    val spawnedCrate: SpawnedCrate
) : PlayerScenario() {

    val future = CompletableFuture<FailAnimation>()

    override val baseLocation: Location = spawnedCrate.location
    override val audience = FilterAudience { p -> p == player }
    override val props: MutableMap<Key, ScenarioProp> = ConcurrentHashMap()

    override fun onTick() {
        tickProps()
        settings.animationTasks[tick]?.executeActions(this) { a, str -> a.updatePlaceholders(str) }

        if (tick > settings.length) {
            val fail = (spawnedCrate.crate as OpenableCrate).animationManager.failAnimations[spawnedCrate]
            fail?.remove(player.uniqueId)

            props.values.forEach { it.onEnd() }
            future.complete(this)
        }
    }

    override val extraPlaceholders: MutableMap<Key, (String) -> String> = ConcurrentHashMap()

    override fun updatePlaceholders(original: String): String {
        var finalString = original.replace("%player%", player.name).updatePAPIPlaceholders(player)

        for ((_, extraPlaceholder) in extraPlaceholders) {
            finalString = extraPlaceholder(finalString)
        }

        return finalString
    }
}