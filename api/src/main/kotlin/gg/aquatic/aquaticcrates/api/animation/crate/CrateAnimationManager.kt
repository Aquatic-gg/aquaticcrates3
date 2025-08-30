package gg.aquatic.aquaticcrates.api.animation.crate

import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate
import gg.aquatic.aquaticcrates.api.reroll.RerollManager
import gg.aquatic.waves.scenario.PlayerScenario
import gg.aquatic.waves.scenario.Scenario
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

abstract class CrateAnimationManager {

    abstract val crate: Crate
    abstract val animationSettings: CrateAnimationSettings
    abstract val rerollManager: RerollManager?

    abstract fun playNewIdleAnimation(spawnedCrate: SpawnedCrate)
    abstract fun playFailAnimation(spawnedCrate: SpawnedCrate, player: Player)

    abstract val playingAnimations: ConcurrentHashMap<UUID, MutableSet<CrateAnimation>>
    abstract val idleAnimation: ConcurrentHashMap<SpawnedCrate, Scenario>
    abstract val failAnimations: ConcurrentHashMap<SpawnedCrate,ConcurrentHashMap<UUID, PlayerScenario>>
    abstract fun playAnimation(animation: CrateAnimation)

    abstract fun skipAnimation(player: Player)

    abstract fun forceStopAnimation(player: Player)
    abstract fun forceStopAnimations()

    abstract fun forceStopAllAnimationTypes(player: Player)
    abstract fun forceStopAllAnimations()

    abstract fun tick()

}