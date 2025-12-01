package gg.aquatic.aquaticcrates.plugin.animation.open

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationManager
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationSettings
import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate
import gg.aquatic.aquaticcrates.api.reroll.RerollManager
import gg.aquatic.aquaticcrates.plugin.CratesPlugin
import gg.aquatic.aquaticcrates.plugin.animation.fail.FailAnimationSettings
import gg.aquatic.aquaticcrates.plugin.animation.idle.IdleAnimationSettings
import gg.aquatic.waves.scenario.PlayerScenario
import gg.aquatic.waves.scenario.Scenario
import gg.aquatic.waves.util.chance.randomItem
import gg.aquatic.waves.util.task.AsyncCtx
import kotlinx.coroutines.*
import org.bukkit.entity.Player
import java.lang.Runnable
import java.lang.Thread
import java.util.*
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

class AnimationManagerImpl(
    override val crate: OpenableCrate,
    override val animationSettings: CrateAnimationSettings,
    private val idleAnimationSettings: Collection<IdleAnimationSettings>,
    private val failAnimationSettings: FailAnimationSettings?,
    rerollManager: (OpenableCrate) -> RerollManager?,
) : CrateAnimationManager() {
    override val rerollManager = rerollManager(crate)

    private val playingAnimations: HashMap<UUID, MutableSet<CrateAnimation>> = HashMap()
    private var idleAnimation: HashMap<SpawnedCrate, Scenario> = HashMap()

    private val failAnimations: HashMap<SpawnedCrate, HashMap<UUID, PlayerScenario>> =
        HashMap()

    override suspend fun stopFailAnimations(crate: SpawnedCrate) = withContext(AnimationCtx) {
        val animations = failAnimations.remove(crate) ?: return@withContext
        for (value in animations.values) {
            value.props.forEach { it.value.onEnd() }
        }
        animations.clear()
    }

    override suspend fun stopFailAnimation(crate: SpawnedCrate, player: Player): Boolean = withContext(AnimationCtx) {
        val animations = failAnimations[crate] ?: return@withContext false
        val animation = animations.remove(player.uniqueId) ?: return@withContext false
        animation.props.values.forEach { it.onEnd() }

        return@withContext true
    }

    override suspend fun failAnimations(): HashMap<SpawnedCrate, HashMap<UUID, PlayerScenario>> =
        withContext(AnimationCtx) {
            return@withContext failAnimations
        }

    override suspend fun stopPlayingAnimation(player: Player, animation: CrateAnimation) = withContext(AnimationCtx) {
        val playerAnimations = playingAnimations[player.uniqueId]
        playerAnimations?.remove(animation)
        if (playerAnimations?.isEmpty() == true) playingAnimations.remove(player.uniqueId)
        animation.destroy()
    }

    override suspend fun playingAnimations(): HashMap<UUID, MutableSet<CrateAnimation>> =
        withContext(AnimationCtx) {
            return@withContext playingAnimations
        }

    override suspend fun stopIdleAnimations(crate: SpawnedCrate): Unit = withContext(AnimationCtx) {
        idleAnimation.remove(crate)?.props?.values?.forEach { it.onEnd() }
    }

    override fun playingAnimationsUnsafe(): Map<UUID, MutableSet<CrateAnimation>> =
        playingAnimations

    override suspend fun idleAnimation(): HashMap<SpawnedCrate, Scenario> = withContext(AnimationCtx) {
        return@withContext idleAnimation
    }

    override suspend fun playNewIdleAnimation(spawnedCrate: SpawnedCrate): Unit = withContext(AnimationCtx) {
        val animation = idleAnimationSettings.randomItem() ?: return@withContext
        idleAnimation[spawnedCrate] = animation.create(spawnedCrate)
    }

    override suspend fun playFailAnimation(spawnedCrate: SpawnedCrate, player: Player): Unit =
        withContext(AnimationCtx) {
            val fail = failAnimations.getOrPut(spawnedCrate) { HashMap() }
            val previousAnimation = fail.remove(player.uniqueId)
            previousAnimation?.props?.values?.forEach { it.onEnd() }

            val new = failAnimationSettings?.create(spawnedCrate, player) ?: return@withContext
            fail[player.uniqueId] = new
        }

    override suspend fun playAnimation(animation: CrateAnimation): Unit = withContext(AnimationCtx) {
        val spawnedCrate = CrateHandler.spawned[animation.baseLocation]
        if (spawnedCrate != null) {
            val fail = failAnimations[spawnedCrate]?.remove(animation.player.uniqueId)
            fail?.props?.values?.forEach { it.onEnd() }
        }
        val animations = playingAnimations.getOrPut(animation.player.uniqueId) { HashSet() }
        animations += animation
    }

    override suspend fun tick() = withContext(AnimationCtx) {
        try {
            for ((_, animations) in playingAnimations) {
                for (animation in animations.toMutableList()) {
                    animation.tick()
                }
            }
            for ((_, animation) in idleAnimation) {
                animation.tick()
            }
            for (entry in failAnimations) {
                for ((_, animation) in entry.value.toMutableMap()) {
                    animation.tick()
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    override suspend fun skipAnimation(player: Player): Unit = withContext(AnimationCtx) {
        playingAnimations[player.uniqueId]?.forEach { it.skip() }
    }

    override suspend fun forceStopAnimation(player: Player): Unit = withContext(AnimationCtx) {
        val animations = playingAnimations[player.uniqueId] ?: return@withContext
        for (animation in animations) {
            animation.finalizeAnimation()
        }
    }

    override suspend fun forceStopAnimations(): Unit = withContext(AnimationCtx) {
        for ((_, animations) in playingAnimations) {
            for (animation in animations) {
                animation.rewards.forEach { reward -> reward.give(animation.player) }
                AsyncCtx {
                    for (value in animation.props.values) {
                        value.onEnd()
                    }
                }
            }
        }
        playingAnimations.clear()
    }

    override suspend fun forceStopAllAnimationTypes(player: Player): Unit = withContext(AnimationCtx) {
        forceStopAnimation(player)
        for ((_, map) in failAnimations) {
            val animation = map.remove(player.uniqueId) ?: continue
            animation.props.values.forEach { it.onEnd() }
        }
    }

    override suspend fun forceStopAllAnimations(): Unit = withContext(AnimationCtx) {
        forceStopAnimations()

        for ((_, map) in failAnimations) {
            for ((_, animation) in map) {
                animation.props.values.forEach { it.onEnd() }
            }
        }
        playingAnimations.clear()

        failAnimations.clear()
        for ((_, animation) in idleAnimation) {
            animation.props.values.forEach { it.onEnd() }
        }
        idleAnimation.clear()
    }

    object AnimationCtx : CoroutineDispatcher() {

        // Single worker thread dedicated to cache operations
        private val executor = Executors.newSingleThreadExecutor(
            Thread.ofPlatform()
                .name("Animation-Worker", 0)
                .daemon(true)
                .uncaughtExceptionHandler { t, e ->
                    CratesPlugin.getInstance().logger.severe("Unhandled exception on $t in CacheCtx")
                    e.printStackTrace()
                }
                .factory()
        )

        val scope = CoroutineScope(
            this + SupervisorJob() + CoroutineExceptionHandler { _, e ->
                CratesPlugin.getInstance().logger.severe("Coroutine exception in CacheCtx")
                e.printStackTrace()
            }
        )

        override fun isDispatchNeeded(context: CoroutineContext): Boolean = true

        override fun dispatch(context: CoroutineContext, block: Runnable) {
            executor.execute(block)
        }

        // Public helpers

        fun launch(block: suspend CoroutineScope.() -> Unit) = scope.launch(block = block)

        fun post(task: () -> Unit) {
            executor.execute(task)
        }

        fun shutdown() {
            executor.shutdown()
        }
    }
}