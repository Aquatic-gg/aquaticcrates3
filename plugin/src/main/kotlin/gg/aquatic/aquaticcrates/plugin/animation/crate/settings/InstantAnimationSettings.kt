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
    override val finalAnimationTasks: MutableList<ConfiguredAction<CrateAnimation>>,
) : CrateAnimationSettings() {


    override val animationTasks: TreeMap<Int, MutableList<ConfiguredAction<CrateAnimation>>> = TreeMap()
    override val animationLength: Int = 0
    override val preAnimationDelay: Int = 0
    override val preAnimationTasks: TreeMap<Int, MutableList<ConfiguredAction<CrateAnimation>>> = TreeMap()
    override val postAnimationDelay: Int = 0
    override val postAnimationTasks: TreeMap<Int, MutableList<ConfiguredAction<CrateAnimation>>> = TreeMap()
    override val skippable: Boolean = false

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
        override fun serialize(section: ConfigurationSection?): CrateAnimationSettings {
            if (section == null) return InstantAnimationSettings(mutableListOf())
            val finalAnimationTasks = loadFinalActions(section)
            return InstantAnimationSettings(
                finalAnimationTasks,
            )
        }

    }
}