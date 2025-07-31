package gg.aquatic.aquaticcrates.plugin.animation.fail

import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate
import gg.aquatic.aquaticcrates.plugin.animation.fail.settings.FailAnimationSettings
import gg.aquatic.waves.util.audience.FilterAudience
import gg.aquatic.waves.util.collection.executeActions
import gg.aquatic.waves.util.updatePAPIPlaceholders
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

class FailAnimation(
    val settings: FailAnimationSettings,
    override val player: Player,
    val spawnedCrate: SpawnedCrate
) : PlayerBoundAnimation() {

    val future = CompletableFuture<FailAnimation>()

    override val baseLocation: Location = spawnedCrate.location
    override val audience = FilterAudience { p -> p == player }

    override val props: MutableMap<String, AnimationProp> = ConcurrentHashMap()
    override fun tick() {
        for ((_, prop) in props) {
            prop.tick()
        }
        settings.animationTasks[tick]?.executeActions(this) { a, str -> a.updatePlaceholders(str) }
        tick++

        if (tick > settings.length) {
            val fail = (spawnedCrate.crate as OpenableCrate).animationManager.failAnimations[spawnedCrate]
            fail?.remove(player.uniqueId)

            props.values.forEach { it.onAnimationEnd() }
            future.complete(this)
        }
    }

    override fun updatePlaceholders(str: String): String {
        var finalString = str.replace("%player%", player.name).updatePAPIPlaceholders(player)

        for ((_, extraPlaceholder) in extraPlaceholders) {
            finalString = extraPlaceholder(finalString)
        }

        return finalString
    }
}