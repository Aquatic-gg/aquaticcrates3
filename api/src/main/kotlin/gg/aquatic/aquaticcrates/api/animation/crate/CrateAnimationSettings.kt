package gg.aquatic.aquaticcrates.api.animation.crate

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture

abstract class CrateAnimationSettings {

    abstract val animationTasks: TreeMap<Int, MutableList<ConfiguredExecutableObject<Animation,Unit>>>
    abstract val animationLength: Int
    abstract val preAnimationDelay: Int
    abstract val preAnimationTasks: TreeMap<Int, MutableList<ConfiguredExecutableObject<Animation,Unit>>>
    abstract val postAnimationDelay: Int
    abstract val postAnimationTasks: TreeMap<Int, MutableList<ConfiguredExecutableObject<Animation,Unit>>>
    abstract val finalAnimationTasks: MutableList<ConfiguredExecutableObject<Animation,Unit>>
    abstract val skippable: Boolean

    abstract fun create(player: Player, animationManager: CrateAnimationManager, location: Location, rolledRewards: MutableList<RolledReward>): CompletableFuture<Void>

}