package gg.aquatic.aquaticcrates.plugin.animation.open.settings

import gg.aquatic.aquaticcrates.api.animation.crate.*
import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.aquaticcrates.plugin.animation.open.RegularAnimationImpl
import gg.aquatic.waves.util.audience.FilterAudience
import gg.aquatic.waves.util.audience.GlobalAudience
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import gg.aquatic.waves.util.runLaterSync
import gg.aquatic.waves.util.task.AsyncCtx
import gg.aquatic.waves.util.task.BukkitCtx
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture

class RegularAnimationSettings(
    override val animationTasks: TreeMap<Int, Collection<ConfiguredExecutableObject<CrateAnimation, Unit>>>,
    override val animationLength: Int,
    override val preAnimationDelay: Int,
    override val preAnimationTasks: TreeMap<Int, Collection<ConfiguredExecutableObject<CrateAnimation, Unit>>>,
    override val postAnimationDelay: Int,
    override val postAnimationTasks: TreeMap<Int, Collection<ConfiguredExecutableObject<CrateAnimation, Unit>>>,
    override val finalAnimationTasks: Collection<ConfiguredExecutableObject<CrateAnimation, Unit>>,
    override val skippable: Boolean,
    val personal: Boolean,
    override val variables: Map<String, String>,
) : CrateAnimationSettings() {

    override suspend fun create(
        player: Player,
        animationManager: CrateAnimationManager,
        location: Location,
        rolledRewards: MutableList<RolledReward>
    ): CrateAnimation = withContext(AsyncCtx) {
        val futureValue = CompletableFuture<CrateAnimation>()
        val animation = RegularAnimationImpl(
            player,
            animationManager,
            location,
            rolledRewards,
            if (personal) FilterAudience {
                if (it == player) {
                    if (futureValue.get().phase !is CrateAnimation.FinalPhase) {
                        return@FilterAudience true
                    } else {
                        for (prop in futureValue.get().props.values) {
                            prop.onEnd()
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
        if (!personal) {
            spawnedCrate?.forceHide(true)
        } else {
            spawnedCrate?.forceHide(player, true)
        }

        animationManager.playAnimation(animation)
        animation.completionFuture.thenRun {
            BukkitCtx {
                if (!personal) {
                    spawnedCrate?.forceHide(false)
                } else {
                    spawnedCrate?.forceHide(player, false)
                }
            }
        }

        animation
    }

    override suspend fun canBeOpened(
        player: Player,
        animationManager: CrateAnimationManager,
        location: Location
    ): AnimationResult {

        val animations = animationManager.playingAnimations()
        if (animations.isNotEmpty() && !personal) return AnimationResult.ALREADY_BEING_OPENED_OTHER
        if (personal && animations.containsKey(player.uniqueId)) return AnimationResult.ALREADY_BEING_OPENED
        return AnimationResult.SUCCESS
    }

    companion object : AnimationSettingsFactory() {
        override fun serialize(section: ConfigurationSection?): CrateAnimationSettings? {
            if (section == null) return null
            val duration = loadAnimationLength(section)
            val delay = loadPreAnimationDelay(section)
            val postDelay = loadPostAnimationDelay(section)
            val variables = HashMap<String, String>()
            section.getConfigurationSection("variables")?.let {
                it.getKeys(false).forEach { key ->
                    variables[key] = it.getString(key) ?: ""
                }
            }
            return RegularAnimationSettings(
                loadAnimationTasks(section.getConfigurationSection("tasks"), duration),
                duration,
                delay,
                loadPreAnimationTasks(section, delay),
                postDelay,
                loadPostAnimationTasks(section, postDelay),
                loadFinalActions(section),
                loadSkippable(section),
                loadIsPersonal(section),
                variables
            )
        }

    }
}