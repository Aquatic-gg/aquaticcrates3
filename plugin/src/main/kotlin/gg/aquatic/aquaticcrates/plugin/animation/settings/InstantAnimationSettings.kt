package gg.aquatic.aquaticcrates.plugin.animation.settings

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.crate.AnimationSettingsFactory
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationManager
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationSettings
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.waves.util.audience.AquaticAudience
import gg.aquatic.waves.util.audience.GlobalAudience
import gg.aquatic.waves.util.executeActions
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture

class InstantAnimationSettings(
    override val finalAnimationTasks: MutableList<ConfiguredExecutableObject<Animation,Unit>>,
) : CrateAnimationSettings() {


    override val animationTasks: TreeMap<Int, MutableList<ConfiguredExecutableObject<Animation,Unit>>> = TreeMap()
    override val animationLength: Int = 0
    override val preAnimationDelay: Int = 0
    override val preAnimationTasks: TreeMap<Int, MutableList<ConfiguredExecutableObject<Animation,Unit>>> = TreeMap()
    override val postAnimationDelay: Int = 0
    override val postAnimationTasks: TreeMap<Int, MutableList<ConfiguredExecutableObject<Animation,Unit>>> = TreeMap()
    override val skippable: Boolean = false

    override fun create(
        player: Player,
        animationManager: CrateAnimationManager,
        location: Location,
        rolledRewards: MutableList<RolledReward>
    ): CompletableFuture<Void> {
        for (rolledReward in rolledRewards) {
            rolledReward.give(player, false)
        }

        execute(player, animationManager)
        return CompletableFuture.completedFuture(null)
    }

    companion object : AnimationSettingsFactory() {
        override fun serialize(section: ConfigurationSection?): CrateAnimationSettings {
            if (section == null) return InstantAnimationSettings(mutableListOf())
            val finalAnimationTasks = loadFinalActions(section)
            return InstantAnimationSettings(
                finalAnimationTasks,
            )
        }

        fun execute(
            player: Player,
            animationManager: CrateAnimationManager
        ) {
            val finalAnimationTasks = animationManager.animationSettings.finalAnimationTasks
            finalAnimationTasks.executeActions(
                object : CrateAnimation() {
                    override val animationManager: CrateAnimationManager = animationManager
                    override val state: State = State.FINISHED
                    override fun skip() {

                    }

                    override val baseLocation: Location = player.location
                    override val player: Player = player
                    override val audience: AquaticAudience = GlobalAudience()
                    override val rewards: MutableList<RolledReward> = mutableListOf()
                    override val props: MutableMap<String, AnimationProp> = mutableMapOf()

                    override fun tick() {
                    }
                }
            ) { _, str -> str }
        }
    }
}