package gg.aquatic.aquaticcrates.api.animation.crate

import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate
import gg.aquatic.aquaticcrates.api.reroll.RerollManager
import gg.aquatic.waves.scenario.PlayerScenario
import gg.aquatic.waves.scenario.Scenario
import org.bukkit.entity.Player
import java.util.*

abstract class CrateAnimationManager {

    abstract val crate: Crate
    abstract val animationSettings: CrateAnimationSettings
    abstract val rerollManager: RerollManager?

    abstract suspend fun playNewIdleAnimation(spawnedCrate: SpawnedCrate)
    abstract suspend fun playFailAnimation(spawnedCrate: SpawnedCrate, player: Player)

    abstract suspend fun playingAnimations(): HashMap<UUID, MutableSet<CrateAnimation>>
    abstract fun playingAnimationsUnsafe(): Map<UUID, MutableSet<CrateAnimation>>

    abstract suspend fun stopPlayingAnimation(player: Player, animation: CrateAnimation)

    abstract suspend fun idleAnimation(): HashMap<SpawnedCrate, Scenario>
    abstract suspend fun stopIdleAnimations(crate: SpawnedCrate)
    abstract suspend fun failAnimations(): HashMap<SpawnedCrate,HashMap<UUID, PlayerScenario>>
    abstract suspend fun stopFailAnimations(crate: SpawnedCrate)
    abstract suspend fun stopFailAnimation(crate: SpawnedCrate, player: Player): Boolean

    abstract suspend fun playAnimation(animation: CrateAnimation)

    abstract suspend fun skipAnimation(player: Player)

    abstract suspend fun forceStopAnimation(player: Player)
    abstract suspend fun forceStopAnimations()

    abstract suspend fun forceStopAllAnimationTypes(player: Player)
    abstract suspend fun forceStopAllAnimations()

    abstract suspend fun tick()

}