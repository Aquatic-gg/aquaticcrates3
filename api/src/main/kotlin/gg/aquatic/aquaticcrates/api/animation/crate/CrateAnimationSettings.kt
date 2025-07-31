package gg.aquatic.aquaticcrates.api.animation.crate

import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture

abstract class CrateAnimationSettings {

    abstract val animationTasks: TreeMap<Int, Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>>
    abstract val animationLength: Int
    abstract val preAnimationDelay: Int
    abstract val preAnimationTasks: TreeMap<Int, Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>>
    abstract val postAnimationDelay: Int
    abstract val postAnimationTasks: TreeMap<Int, Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>>
    abstract val finalAnimationTasks: Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>
    abstract val skippable: Boolean

    abstract fun create(player: Player, animationManager: CrateAnimationManager, location: Location, rolledRewards: MutableList<RolledReward>): CompletableFuture<CrateAnimation>

    abstract fun canBeOpened(player: Player, animationManager: CrateAnimationManager, location: Location): AnimationResult

    enum class AnimationResult {
        ALREADY_BEING_OPENED,
        ALREADY_BEING_OPENED_OTHER,
        SUCCESS,
    }
}