package gg.aquatic.aquaticcrates.plugin.animation.pouch.settings

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.pouch.PouchAnimation
import gg.aquatic.aquaticcrates.api.animation.pouch.PouchAnimationManager
import gg.aquatic.aquaticcrates.api.animation.pouch.PouchAnimationSettings
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.aquaticcrates.plugin.animation.pouch.RegularPouchAnimationImpl
import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import gg.aquatic.aquaticseries.lib.audience.FilterAudience
import gg.aquatic.aquaticseries.lib.audience.GlobalAudience
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

class PouchRegularAnimationSettings(
    override val animationTasks: TreeMap<Int, MutableList<ConfiguredAction<Animation>>>,
    override val animationLength: Int,
    override val preAnimationDelay: Int,
    override val preAnimationTasks: TreeMap<Int, MutableList<ConfiguredAction<Animation>>>,
    override val postAnimationDelay: Int,
    override val postAnimationTasks: TreeMap<Int, MutableList<ConfiguredAction<Animation>>>,
    override val finalAnimationTasks: MutableList<ConfiguredAction<Animation>>,
    override val skippable: Boolean,
    val personal: Boolean,
) : PouchAnimationSettings() {
    override fun create(player: Player, animationManager: PouchAnimationManager, location: Location, rolledRewards: MutableList<RolledReward>): PouchAnimation {
        return RegularPouchAnimationImpl(player, animationManager, location, rolledRewards, if (personal) FilterAudience { it == player} else GlobalAudience())
    }
}