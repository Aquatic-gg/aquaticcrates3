package gg.aquatic.aquaticcrates.plugin.animation.open

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationManager
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.waves.scenario.ScenarioProp
import gg.aquatic.waves.util.audience.AquaticAudience
import gg.aquatic.waves.util.task.BukkitCtx
import net.kyori.adventure.key.Key
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

class RegularAnimationImpl(
    override val player: Player,
    override val animationManager: CrateAnimationManager,
    override val baseLocation: Location,
    override val rewards: MutableList<RolledReward>,
    override val audience: AquaticAudience,
    override val completionFuture: CompletableFuture<CrateAnimation>
) : CrateAnimation() {

    override val props: MutableMap<Key, ScenarioProp> = ConcurrentHashMap()
    override val extraPlaceholders: MutableMap<Key, (String) -> String> = ConcurrentHashMap()
    override val settings = animationManager.animationSettings

    init {
        for ((id, value) in settings.variables) {
            extraPlaceholders[Key.key("variable:$id")] = { str -> str.replace("%variable:$id%", value) }
        }
    }

    override fun onReroll() {
        val crate = animationManager.crate as OpenableCrate
        val rerollManager = animationManager.rerollManager!!

        rerollManager.openReroll(player, this, rewards).thenAcceptAsync { result ->
            AnimationManagerImpl.AnimationCtx.launch {
                if (result.reroll) {
                    updatePhase(OpeningPhase())
                    rewards.clear()
                    for ((_, prop) in props) {
                        prop.onEnd()
                    }
                    props.clear()
                    rewards += crate.rewardManager.getRewards(player)
                    tick()
                } else {
                    updatePhase(PostOpenPhase())
                    tick()
                }
                usedRerolls++
            }
        }.exceptionally {
            it.printStackTrace()
            null
        }
    }

    override fun onFinalize() {
        val runnable = {
            player.updateInventory()
        }

        if (Bukkit.isPrimaryThread()) {
            runnable()
        } else {
            BukkitCtx {
                runnable()
            }
        }
    }

}