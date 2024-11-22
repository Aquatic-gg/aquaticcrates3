package gg.aquatic.aquaticcrates.api.animation.crate

import gg.aquatic.aquaticcrates.api.animation.AnimationTitle
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

abstract class CrateAnimationSettings {

    abstract val animationTasks: TreeMap<Int, MutableList<ConfiguredAction<CrateAnimation>>>
    abstract val animationLength: Int
    abstract val preAnimationDelay: Int
    abstract val preAnimationTasks: TreeMap<Int, MutableList<ConfiguredAction<CrateAnimation>>>
    abstract val postAnimationDelay: Int
    abstract val postAnimationTasks: TreeMap<Int, MutableList<ConfiguredAction<CrateAnimation>>>
    abstract val finalAnimationTasks: MutableList<ConfiguredAction<CrateAnimation>>
    abstract val skippable: Boolean
    abstract val openingBossbar: AnimationTitle
    abstract val rerollingBossbar: AnimationTitle

    abstract fun create(player: Player, animationManager: CrateAnimationManager, location: Location, rolledRewards: MutableList<RolledReward>): CrateAnimation

}