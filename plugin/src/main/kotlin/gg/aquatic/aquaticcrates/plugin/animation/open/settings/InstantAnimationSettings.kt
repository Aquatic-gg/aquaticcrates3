package gg.aquatic.aquaticcrates.plugin.animation.open.settings

import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.*
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.waves.util.audience.AquaticAudience
import gg.aquatic.waves.util.audience.GlobalAudience
import gg.aquatic.waves.util.collection.executeActions
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import gg.aquatic.waves.util.updatePAPIPlaceholders
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture

class InstantAnimationSettings(
    override val finalAnimationTasks: Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>,
) : CrateAnimationSettings() {


    override val animationTasks: TreeMap<Int, Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>> =
        TreeMap()
    override val animationLength: Int = 0
    override val preAnimationDelay: Int = 0
    override val preAnimationTasks: TreeMap<Int, Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>> =
        TreeMap()
    override val postAnimationDelay: Int = 0
    override val postAnimationTasks: TreeMap<Int, Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>> =
        TreeMap()
    override val skippable: Boolean = false

    override fun create(
        player: Player,
        animationManager: CrateAnimationManager,
        location: Location,
        rolledRewards: MutableList<RolledReward>
    ): CompletableFuture<CrateAnimation> {
        for (rolledReward in rolledRewards) {
            rolledReward.give(player, false)
        }

        execute(player, animationManager)
        return CompletableFuture.completedFuture(null)
    }

    override fun canBeOpened(
        player: Player,
        animationManager: CrateAnimationManager,
        location: Location
    ): AnimationResult {
        return AnimationResult.SUCCESS
    }

    companion object : AnimationSettingsFactory() {
        override fun serialize(section: ConfigurationSection?): CrateAnimationSettings {
            if (section == null) return InstantAnimationSettings(
                listOf()
            )
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

            val obj = object : CrateAnimation() {
                override val animationManager: CrateAnimationManager = animationManager
                override var state: State = State.FINISHED

                override val baseLocation: Location = player.location
                override val player: Player = player
                override val audience: AquaticAudience = GlobalAudience()
                override val rewards: MutableList<RolledReward> = mutableListOf()
                override val completionFuture: CompletableFuture<CrateAnimation> =
                    CompletableFuture.completedFuture(this)
                override val settings: CrateAnimationSettings = animationManager.animationSettings
                override val props: MutableMap<String, AnimationProp> = mutableMapOf()

                override fun tick() {
                }

                override fun onReroll() {
                }
            }
            finalAnimationTasks.executeActions(obj) { a, str -> a.updatePlaceholders(str) }
        }
    }
}