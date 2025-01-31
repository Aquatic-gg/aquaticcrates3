package gg.aquatic.aquaticcrates.plugin.animation.prop

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationActions
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.reward.Reward

class RumblingRewardProp(
    override val animation: CrateAnimation,
    val rumblingLength: Int,
    val rumblingPeriod: Int,
    val easeOut: Boolean,
    val rewardIndex: Int,
    val onRumbleActions: CrateAnimationActions,
    val onFinishActions: CrateAnimationActions
) : AnimationProp() {

    var tick = -1
        private set

    var finished = false
        private set

    @Volatile
    var currentReward: Reward? = null
        private set

    override fun tick() {
        tick++
        if (finished) {
            return
        }
        if (tick >= rumblingLength) {
            currentReward = animation.rewards.getOrNull(rewardIndex)?.reward ?: animation.rewards.first().reward
            onFinishActions.execute(animation)
            finished = true
            return
        }

        var update = false
        if (!easeOut) {
            if (tick % rumblingPeriod == 0) {
                update = true
            }
        } else if (shouldPerformRumbling(tick, rumblingPeriod, rumblingLength)) {
            update = true
        }

        if (update) {
            val rewards =
                (animation.animationManager.crate as OpenableCrate).rewardManager.getPossibleRewards(animation.player).values.toMutableList()
            if (currentReward != null) {
                rewards.remove(currentReward)
            }
            currentReward = rewards.random()
            onRumbleActions.execute(animation)
            //updateItem(previousReward!!)
        }
    }

    private fun shouldPerformRumbling(tick: Int, period: Int, duration: Int): Boolean {
        val thresholdTick = (duration * 0.5).toInt()

        if (tick < thresholdTick) {
            return tick % period == 0
        } else {
            val ticksSinceThreshold = tick - thresholdTick
            val ticksRemaining = duration - tick

            val easingRatio = (ticksRemaining.toDouble() / (duration - thresholdTick).toDouble())
            val easingInterval = (period.toDouble() * (1 + (1 - easingRatio) * 4)).toInt()

            return ticksSinceThreshold % easingInterval == 0 || tick == duration
        }
    }

    override fun onAnimationEnd() {

    }
}