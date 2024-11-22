package gg.aquatic.aquaticcrates.plugin.animation.crate.settings

import gg.aquatic.aquaticcrates.api.animation.AnimationTitle
import gg.aquatic.aquaticcrates.api.animation.crate.AnimationSettingsFactory
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationManager
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationSettings
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import gg.aquatic.aquaticseries.lib.audience.AquaticAudience
import gg.aquatic.aquaticseries.lib.audience.GlobalAudience
import gg.aquatic.aquaticseries.lib.util.executeActions
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import java.util.*

class InstantAnimationSettings(
    override val animationTasks: TreeMap<Int, MutableList<ConfiguredAction<CrateAnimation>>>,
    override val animationLength: Int,
    override val preAnimationDelay: Int,
    override val preAnimationTasks: TreeMap<Int, MutableList<ConfiguredAction<CrateAnimation>>>,
    override val postAnimationDelay: Int,
    override val postAnimationTasks: TreeMap<Int, MutableList<ConfiguredAction<CrateAnimation>>>,
    override val finalAnimationTasks: MutableList<ConfiguredAction<CrateAnimation>>,
    override val skippable: Boolean,
) : CrateAnimationSettings() {
    override fun create(
        player: Player,
        animationManager: CrateAnimationManager,
        location: Location,
        rolledRewards: MutableList<RolledReward>
    ) {
        for (rolledReward in rolledRewards) {
            rolledReward.give(player)
        }
        finalAnimationTasks.executeActions(
            object : CrateAnimation() {
                override val animationManager: CrateAnimationManager = animationManager
                override val state: State = State.FINISHED
                override val baseLocation: Location = location
                override val player: Player = player
                override val audience: AquaticAudience = GlobalAudience()
                override val rewards: MutableList<RolledReward> = rolledRewards
                override val props: MutableMap<String, AnimationProp> = mutableMapOf()

                override fun tick() {
                }
            }
        ) { _, str -> str }
    }

    companion object: AnimationSettingsFactory() {
        override fun serialize(section: ConfigurationSection): CrateAnimationSettings? {
            val finalAnimationTasks = loadFinalActions(section)
            return InstantAnimationSettings(
                TreeMap(),
                0,
                0,
                TreeMap(),
                0,
                TreeMap(),
                finalAnimationTasks,
                false,
            )
        }

    }
}