package gg.aquatic.aquaticcrates.plugin.animation.open

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationManager
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.aquaticcrates.plugin.animation.open.settings.CinematicAnimationSettings
import gg.aquatic.aquaticcrates.plugin.animation.prop.CameraAnimationProp
import gg.aquatic.waves.scenario.ScenarioProp
import gg.aquatic.waves.util.audience.AquaticAudience
import gg.aquatic.waves.util.task.BukkitCtx
import kotlinx.coroutines.launch
import net.kyori.adventure.key.Key
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

class CinematicAnimationImpl(
    override val player: Player,
    override val animationManager: CrateAnimationManager,
    override val baseLocation: Location,
    override val rewards: MutableList<RolledReward>,
    override val audience: AquaticAudience,
    override val completionFuture: CompletableFuture<CrateAnimation>,
    //val camera: CameraAnimationProp
) : CrateAnimation() {

    override val settings = animationManager.animationSettings as CinematicAnimationSettings

    override val props: MutableMap<Key, ScenarioProp> = ConcurrentHashMap()
    override val extraPlaceholders: MutableMap<Key, (String) -> String> = ConcurrentHashMap()

    init {
        for ((id, value) in settings.variables) {
            extraPlaceholders[Key.key("variable:$id")] = { str -> str.replace("%variable:$id%", value) }
        }
    }

    private var attached = false

    override fun onReroll() {
        val crate = animationManager.crate as OpenableCrate
        val rerollManager = animationManager.rerollManager!!
        rerollManager.openReroll(player, this, rewards).thenAccept { result ->
            if (result.reroll) {
                for ((_, prop) in props) {
                    if (prop is CameraAnimationProp) {
                        prop.boundPaths.clear()
                        continue
                    }
                    prop.onEnd()
                }
                props.toList().forEach { if (it.first != Key.key("camera")) props.remove(it.first) }

                rewards.clear()
                updatePhase(OpeningPhase())

                rewards += crate.rewardManager.getRewards(player)
                tick()
            } else {
                updatePhase(PostOpenPhase())
                tick()
            }

            usedRerolls++
        }
    }

    override fun onFinalize() {

        val prop = prop<CameraAnimationProp>(Key.key("camera"))!!
        val runnable = {
            try {
                player.isInvisible = false
                prop.detach()
                player.gameMode = prop.previousGamemode
                player.teleport(prop.previousLocation)

                player.updateInventory()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        if (Bukkit.isPrimaryThread()) {
            runnable()
        } else {
            BukkitCtx {
                runnable()
            }
        }
    }

    override fun onPhaseUpdate(phase: Phase) {
        if (phase is OpeningPhase && !attached) {
            attached = true
            val cameraProp = prop<CameraAnimationProp>(Key.key("camera"))!!
            cameraProp.attachPlayer()
        }
    }



}