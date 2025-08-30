package gg.aquatic.aquaticcrates.plugin.animation.prop

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.animation.prop.ItemBasedProp
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.scenario.ScenarioProp
import gg.aquatic.waves.util.collection.executeActions
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject

class RumblingRewardProp(
    override val scenario: CrateAnimation,
    val rumblingLength: Int,
    val rumblingPeriod: Int,
    val easeOut: Boolean,
    val rewardIndex: Int,
    val onRumbleActions: Collection<ConfiguredExecutableObject<CrateAnimation, Unit>>,
    val onFinishActions: Collection<ConfiguredExecutableObject<CrateAnimation, Unit>>
) : ScenarioProp, ItemBasedProp {

    @Volatile
    var tick = -1
        private set

    @Volatile
    private var nextUpdateTick = -1 // Tracks when the next update should occur

    @Volatile
    var finished = false
        private set

    @Volatile
    var currentReward: Reward? = null
        private set

    override fun tick() {
        if (finished) return

        tick++ // Increment the current tick

        // If animation completes, trigger the final update
        if (tick >= rumblingLength) {
            performUpdate(true)
            return
        }

        // Perform an update if we’ve reached the scheduled tick
        if (tick == nextUpdateTick || nextUpdateTick == -1) {
            scheduleNextUpdate() // Calculate the next update tick
            performUpdate(false) // Perform the update
        }
    }

    private fun scheduleNextUpdate() {
        if (!easeOut) {
            // Ease-Out Disabled: Steady updates
            nextUpdateTick = tick + rumblingPeriod
            return
        }
        /*
        // Ease-Out Enabled
        val thresholdTick = (rumblingLength * 0.5).toInt() // Ease-out starts at the halfway point

        nextUpdateTick = if (tick <= thresholdTick) {
            // Steady phase: Regular updates before halfway point
            tick + rumblingPeriod
        } else {
            // Ease-out phase: Gradually increase intervals based on remaining time
            val ticksRemaining = rumblingLength - tick
            val easeOutTicksRemaining = rumblingLength - thresholdTick
            val easeRatio = (ticksRemaining.toDouble() / easeOutTicksRemaining.toDouble())
                .coerceIn(0.0, 1.0)

            // Gradually increase delay based on the easing ratio
            val interval = (rumblingPeriod + (10 * (1.0 - easeRatio))).toInt()
                .coerceAtLeast(1) // Ensure at least 1-tick interval
            tick + interval
        }
         */

        // Ease-Out Enabled
        val thresholdTick = (rumblingLength * 0.5).toInt() // Halfway point
        val ticksRemaining = rumblingLength - tick

        if (tick <= thresholdTick) {
            // Steady phase: Fixed interval updates before halfway
            nextUpdateTick = tick + rumblingPeriod
        } else {
            // Ease-out phase: Non-linear easing slows updates as we near the end
            val totalEaseTicks = rumblingLength - thresholdTick
            val easeProgress = (ticksRemaining.toDouble() / totalEaseTicks) // 1.0 -> 0.0

            // Use a non-linear scaling function for easing (quadratic)
            // interval grows larger as easeProgress decreases
            val interval = (rumblingPeriod + 10 * (1 - easeProgress * easeProgress)).toInt()
                .coerceAtLeast(1) // At least 1-tick interval

            // Schedule the next update
            nextUpdateTick = (tick + interval).coerceAtMost(rumblingLength)
        }
    }

    private fun performUpdate(final: Boolean) {
        if (final) {
            onUpdate(scenario.rewards.getOrNull(rewardIndex)?.reward ?: scenario.rewards.first().reward, true)
            finished = true
            return
        }

        val rewards = (scenario.animationManager.crate as OpenableCrate).rewardManager
            .getPossibleRewards(scenario.player).values.toMutableList()

        if (currentReward != null && rewards.size > 1) rewards.remove(currentReward)

        onUpdate(rewards.random(), false)
    }

    private fun onUpdate(reward: Reward, final: Boolean) {
        currentReward = reward
        if (final) {
            onFinishActions.executeActions(scenario) { a, str -> a.updatePlaceholders(str) }
            finished = true
        } else {
            onRumbleActions.executeActions(scenario) { a, str -> a.updatePlaceholders(str) }
        }
    }

    /*
    private fun shouldPerformRumbling(period: Int, duration: Int): Boolean {
        /*
        val thresholdTick = (duration * 0.5).toInt()

        if (tick <= thresholdTick) {
            return tick % period == 0
        } else {
            val ticksSinceThreshold = tick - thresholdTick
            val ticksRemaining = duration - tick

            val easingRatio = (ticksRemaining.toDouble() / (duration.toDouble() - thresholdTick))
            val easingInterval = (period.toDouble() * (1.0 + (1.0 - easingRatio) * 4.0)).toInt()

            return ticksSinceThreshold % easingInterval == 0 || tick == duration
        }
         */

        val thresholdTick = (duration * 0.5).toInt() // Ease-out starts at half the duration

        if (tick <= thresholdTick) {
            // Normal rumbling phase (steady period)
            return tick % period == 0
        } else {
            // Ease-out phase
            val ticksSinceThreshold = tick - thresholdTick
            val ticksRemaining = duration - tick

            // Clamp easingRatio to avoid instability and smooth updates
            val easingRatio = ticksRemaining.toDouble().coerceAtLeast(0.0) / (duration - thresholdTick).toDouble()

            // Calculate easing interval and clamp to avoid extreme or redundant intervals
            val easingInterval = (period * (1.0 + (1.0 - easingRatio) * 4.0)).toInt().coerceAtLeast(period)

            println("Tick: $tick, Threshold: $thresholdTick, Remaining: $ticksRemaining, EasingRatio: $easingRatio, EasingInterval: $easingInterval")
            // Ensure updates happen only at evenly distributed points
            return (ticksSinceThreshold % easingInterval == 0 && tick != duration) || tick == duration
        }


    }
     */

    override fun onEnd() {

    }

    override fun item(): AquaticItem? {
        return currentReward?.item
    }
}