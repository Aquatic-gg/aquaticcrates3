package gg.aquatic.aquaticcrates.plugin.animation.fail.settings

import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate
import gg.aquatic.aquaticcrates.plugin.animation.fail.FailAnimation
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import gg.aquatic.waves.util.runLaterSync
import gg.aquatic.waves.util.runSync
import org.bukkit.entity.Player
import java.util.*

class FailAnimationSettings (
    val animationTasks: TreeMap<Int, Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>>,
    val length: Int,
){

    fun create(spawnedCrate: SpawnedCrate, player: Player): FailAnimation {
        val animation = FailAnimation(
            this,
            player,
            spawnedCrate
        )

        animation.tick()
        runLaterSync(1) {
            spawnedCrate.forceHide(player, true)
        }

        animation.future.thenRun {
            runSync {
                spawnedCrate.forceHide(player, false)
            }
        }

        return animation
    }

}