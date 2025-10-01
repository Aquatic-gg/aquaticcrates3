package gg.aquatic.aquaticcrates.plugin.animation.open.settings

import gg.aquatic.aquaticcrates.api.animation.crate.AnimationSettingsFactory
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationManager
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationSettings
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.waves.util.audience.AquaticAudience
import gg.aquatic.waves.util.audience.GlobalAudience
import gg.aquatic.waves.util.collection.executeActions
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import gg.aquatic.waves.util.task.AsyncCtx
import kotlinx.coroutines.withContext
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture

class InstantAnimationSettings(
    override val finalAnimationTasks: Collection<ConfiguredExecutableObject<CrateAnimation, Unit>>,
) : CrateAnimationSettings() {


    override val animationTasks: TreeMap<Int, Collection<ConfiguredExecutableObject<CrateAnimation, Unit>>> =
        TreeMap()
    override val animationLength: Int = 0
    override val preAnimationDelay: Int = 0
    override val preAnimationTasks: TreeMap<Int, Collection<ConfiguredExecutableObject<CrateAnimation, Unit>>> =
        TreeMap()
    override val postAnimationDelay: Int = 0
    override val postAnimationTasks: TreeMap<Int, Collection<ConfiguredExecutableObject<CrateAnimation, Unit>>> =
        TreeMap()
    override val skippable: Boolean = false
    override val variables: Map<String, String> = mapOf()

    override suspend fun create(
        player: Player,
        animationManager: CrateAnimationManager,
        location: Location,
        rolledRewards: MutableList<RolledReward>
    ): CrateAnimation = withContext(AsyncCtx) {
        for (rolledReward in rolledRewards) {
            rolledReward.give(player, false)
        }

        val anim = execute(player, animationManager)
        anim.rewards += rolledRewards
        return@withContext anim
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
        ): CrateAnimation {
            val finalAnimationTasks = animationManager.animationSettings.finalAnimationTasks

            val obj = object : CrateAnimation() {
                override val animationManager: CrateAnimationManager = animationManager

                override val baseLocation: Location = player.location
                override val player: Player = player
                override val audience: AquaticAudience = GlobalAudience()
                override val rewards: MutableList<RolledReward> = mutableListOf()
                override val completionFuture: CompletableFuture<CrateAnimation> =
                    CompletableFuture.completedFuture(this)
                override val settings: CrateAnimationSettings = animationManager.animationSettings
                override fun onReroll() {

                }
            }
            finalAnimationTasks.executeActions(obj) { a, str -> a.updatePlaceholders(str) }
            return obj
        }
    }
}