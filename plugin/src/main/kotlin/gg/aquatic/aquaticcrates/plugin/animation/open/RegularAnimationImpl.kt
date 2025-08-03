package gg.aquatic.aquaticcrates.plugin.animation.open

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationManager
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.waves.util.audience.AquaticAudience
import gg.aquatic.waves.util.runSync
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
    @Volatile
    override var state: State = State.PRE_OPEN

    override val settings = animationManager.animationSettings
    override val props: ConcurrentHashMap<String, AnimationProp> = ConcurrentHashMap()

    override fun onReroll() {
        val crate = animationManager.crate as OpenableCrate
        val rerollManager = animationManager.rerollManager!!

        rerollManager.openReroll(player, this, rewards).thenAcceptAsync { result ->
            if (result.reroll) {
                updateState(State.OPENING)
                rewards.clear()
                for ((_, prop) in props) {
                    prop.onAnimationEnd()
                }
                props.clear()
                rewards += crate.rewardManager.getRewards(player)
                tick()
            } else {
                updateState(State.POST_OPEN)
                tick()
            }
            usedRerolls++
        }.exceptionally {
            it.printStackTrace()
            null
        }
    }

    override fun onFinalize(isSync: Boolean) {
        val runnable = {
            player.updateInventory()
        }

        if (isSync) {
            runnable()
        } else {
            runSync {
                runnable()
            }
        }
    }
}