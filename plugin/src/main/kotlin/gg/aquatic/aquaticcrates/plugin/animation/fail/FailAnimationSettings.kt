package gg.aquatic.aquaticcrates.plugin.animation.fail

import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate
import gg.aquatic.aquaticcrates.plugin.animation.open.AnimationManagerImpl
import gg.aquatic.waves.scenario.PlayerScenario
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import gg.aquatic.waves.util.runLaterSync
import org.bukkit.entity.Player
import java.util.*

class FailAnimationSettings (
    val animationTasks: TreeMap<Int, Collection<ConfiguredExecutableObject<PlayerScenario, Unit>>>,
    val length: Int,
){

    fun create(spawnedCrate: SpawnedCrate, player: Player): FailAnimation {
        val animation = FailAnimation(
            this,
            player,
            spawnedCrate
        )

        AnimationManagerImpl.AnimationCtx.launch {
            animation.tick()
            runLaterSync(1) {
                spawnedCrate.forceHide(player, true)
            }

            animation.future.thenRun {
                spawnedCrate.forceHide(player, false)
            }
        }


        return animation
    }

}