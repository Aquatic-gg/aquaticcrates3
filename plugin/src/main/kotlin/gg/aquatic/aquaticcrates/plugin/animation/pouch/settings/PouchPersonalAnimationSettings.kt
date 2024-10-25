package gg.aquatic.aquaticcrates.plugin.animation.pouch.settings

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.pouch.PouchAnimation
import gg.aquatic.aquaticcrates.api.animation.pouch.PouchAnimationManager
import gg.aquatic.aquaticcrates.api.animation.pouch.PouchAnimationSettings
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.aquaticcrates.plugin.animation.pouch.InstantPouchAnimationImpl
import gg.aquatic.aquaticcrates.plugin.animation.pouch.PersonalPouchAnimationImpl
import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

class PouchPersonalAnimationSettings(
    override val animationTasks: TreeMap<Int, MutableList<ConfiguredAction<Animation>>>,
    override val animationLength: Int,
    override val preAnimationDelay: Int,
    override val preAnimationTasks: TreeMap<Int, MutableList<ConfiguredAction<Animation>>>,
    override val postAnimationDelay: Int,
    override val postAnimationTasks: TreeMap<Int, MutableList<ConfiguredAction<Animation>>>,
    override val finalAnimationTasks: MutableList<ConfiguredAction<Animation>>,
    override val skippable: Boolean,
) : PouchAnimationSettings() {
    override fun create(player: Player, animationManager: PouchAnimationManager, location: Location, rolledRewards: MutableList<RolledReward>): PouchAnimation {
        return PersonalPouchAnimationImpl(player, animationManager, location, rolledRewards)
    }
}