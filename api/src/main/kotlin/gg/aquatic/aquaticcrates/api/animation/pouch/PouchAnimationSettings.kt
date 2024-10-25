package gg.aquatic.aquaticcrates.api.animation.pouch

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.AnimationTitle
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

abstract class PouchAnimationSettings {

    abstract val animationTasks: TreeMap<Int, MutableList<ConfiguredAction<Animation>>>
    abstract val animationLength: Int
    abstract val preAnimationDelay: Int
    abstract val preAnimationTasks: TreeMap<Int, MutableList<ConfiguredAction<Animation>>>
    abstract val postAnimationDelay: Int
    abstract val postAnimationTasks: TreeMap<Int, MutableList<ConfiguredAction<Animation>>>
    abstract val finalAnimationTasks: MutableList<ConfiguredAction<Animation>>
    abstract val skippable: Boolean

    abstract fun create(player: Player, animationManager: PouchAnimationManager, location: Location, rolledRewards: MutableList<RolledReward>): PouchAnimation

}