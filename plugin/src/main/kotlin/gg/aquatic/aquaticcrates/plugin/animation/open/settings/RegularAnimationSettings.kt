package gg.aquatic.aquaticcrates.plugin.animation.open.settings

import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.*
import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.aquaticcrates.plugin.animation.open.RegularAnimationImpl
import gg.aquatic.waves.util.audience.FilterAudience
import gg.aquatic.waves.util.audience.GlobalAudience
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import gg.aquatic.waves.util.runLaterSync
import gg.aquatic.waves.util.runSync
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture

class RegularAnimationSettings(
    override val animationTasks: TreeMap<Int, Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>>,
    override val animationLength: Int,
    override val preAnimationDelay: Int,
    override val preAnimationTasks: TreeMap<Int, Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>>,
    override val postAnimationDelay: Int,
    override val postAnimationTasks: TreeMap<Int, Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>>,
    override val finalAnimationTasks: Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>,
    override val skippable: Boolean,
    val personal: Boolean,
) : CrateAnimationSettings() {

    override fun create(
        player: Player,
        animationManager: CrateAnimationManager,
        location: Location,
        rolledRewards: MutableList<RolledReward>
    ): CompletableFuture<CrateAnimation> {
        val futureValue = CompletableFuture<CrateAnimation>()
        val animation = RegularAnimationImpl(
            player,
            animationManager,
            location,
            rolledRewards,
            if (personal) FilterAudience {
                if (it == player) {
                    if (futureValue.get().state != CrateAnimation.State.FINISHED) {
                        return@FilterAudience true
                    } else {
                        for (prop in futureValue.get().props.values) {
                            prop.onAnimationEnd()
                        }
                        futureValue.get().props.clear()
                    }
                }
                false
            } else GlobalAudience(),
            CompletableFuture()
        )
        futureValue.complete(animation)

        val spawnedCrate = CrateHandler.spawned[location]
        animation.tick()
        runLaterSync(1) {
            if (!personal) {
                spawnedCrate?.forceHide(true)
            } else {
                spawnedCrate?.forceHide(player, true)
            }
        }

        animationManager.playAnimation(animation)
        return animation.completionFuture.thenApply { animation ->
            runSync {
                if (!personal) {
                    spawnedCrate?.forceHide(false)
                } else {
                    spawnedCrate?.forceHide(player, false)
                }
            }
            animation
        }
    }

    override fun canBeOpened(
        player: Player,
        animationManager: CrateAnimationManager,
        location: Location
    ): AnimationResult {
        if (animationManager.playingAnimations.isNotEmpty() && !personal) return AnimationResult.ALREADY_BEING_OPENED_OTHER
        if (personal && animationManager.playingAnimations.containsKey(player.uniqueId)) return AnimationResult.ALREADY_BEING_OPENED
        return AnimationResult.SUCCESS
    }

    companion object : AnimationSettingsFactory() {
        override fun serialize(section: ConfigurationSection?): CrateAnimationSettings? {
            if (section == null) return null
            val duration = loadAnimationLength(section)
            val delay = loadPreAnimationDelay(section)
            val postDelay = loadPostAnimationDelay(section)
            return RegularAnimationSettings(
                loadAnimationTasks(section.getConfigurationSection("tasks"), duration),
                duration,
                delay,
                loadPreAnimationTasks(section, delay),
                postDelay,
                loadPostAnimationTasks(section, postDelay),
                loadFinalActions(section),
                loadSkippable(section),
                loadIsPersonal(section)
            )
        }

    }
}