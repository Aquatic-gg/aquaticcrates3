package gg.aquatic.aquaticcrates.plugin.animation.crate.settings

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.crate.AnimationSettingsFactory
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationManager
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationSettings
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.aquaticcrates.plugin.animation.crate.RegularAnimationImpl
import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import gg.aquatic.aquaticseries.lib.audience.FilterAudience
import gg.aquatic.aquaticseries.lib.audience.GlobalAudience
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture

class RegularAnimationSettings(
    override val animationTasks: TreeMap<Int, MutableList<ConfiguredAction<Animation>>>,
    override val animationLength: Int,
    override val preAnimationDelay: Int,
    override val preAnimationTasks: TreeMap<Int, MutableList<ConfiguredAction<Animation>>>,
    override val postAnimationDelay: Int,
    override val postAnimationTasks: TreeMap<Int, MutableList<ConfiguredAction<Animation>>>,
    override val finalAnimationTasks: MutableList<ConfiguredAction<Animation>>,
    override val skippable: Boolean,
    val personal: Boolean,
) : CrateAnimationSettings() {

    override fun create(
        player: Player,
        animationManager: CrateAnimationManager,
        location: Location,
        rolledRewards: MutableList<RolledReward>
    ): CompletableFuture<Void> {
        val animation = RegularAnimationImpl(
            player,
            animationManager,
            location,
            rolledRewards,
            if (personal) FilterAudience { it == player } else GlobalAudience(),
            CompletableFuture()
        )
        animationManager.playAnimation(animation)
        return animation.completionFuture
    }

    companion object: AnimationSettingsFactory() {
        override fun serialize(section: ConfigurationSection?): CrateAnimationSettings? {
            if (section == null) return null
            return RegularAnimationSettings(
                loadAnimationTasks(section.getConfigurationSection("tasks")),
                loadAnimationLength(section),
                loadPreAnimationDelay(section),
                loadPreAnimationTasks(section),
                loadPostAnimationDelay(section),
                loadPostAnimationTasks(section),
                loadFinalActions(section),
                loadSkippable(section),
                loadIsPersonal(section)
            )
        }

    }
}