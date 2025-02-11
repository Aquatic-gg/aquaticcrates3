package gg.aquatic.aquaticcrates.api.animation.crate

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate
import gg.aquatic.aquaticcrates.api.reroll.RerollManager
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
    abstract val idleAnimation: ConcurrentHashMap<SpawnedCrate, Animation>
    abstract val failAnimations: ConcurrentHashMap<SpawnedCrate,ConcurrentHashMap<UUID, PlayerBoundAnimation>>
    abstract fun playAnimation(animation: CrateAnimation)

    abstract fun skipAnimation(player: Player)

    abstract fun forceStopAnimation(player: Player)
    abstract fun forceStopAnimations()

    abstract fun forceStopAllAnimationTypes(player: Player)
    abstract fun forceStopAllAnimations()

    abstract fun tick()

}