package gg.aquatic.aquaticcrates.plugin.animation.prop.showcase

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.api.reward.showcase.RewardShowcase
import gg.aquatic.aquaticcrates.api.reward.showcase.RewardShowcaseHandle
import gg.aquatic.aquaticcrates.api.reward.showcase.item.ItemRewardShowcaseHandle
import gg.aquatic.waves.Waves
import gg.aquatic.waves.scenario.Scenario
import gg.aquatic.waves.scenario.ScenarioProp
import gg.aquatic.waves.scenario.prop.Throwable
import gg.aquatic.waves.util.sendPacket
import org.bukkit.util.Vector

class RewardShowcaseAnimationProp(
    override val scenario: Scenario,
    val locationOffset: Pair<Vector, Pair<Float, Float>>,
    val velocity: Vector,
) : ScenarioProp, Throwable {

    var showcaseHandle: RewardShowcaseHandle<*>? = null

    override fun tick() {
    }

    override fun onEnd() {
        showcaseHandle?.destroy()
        showcaseHandle = null
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : RewardShowcase> update(reward: Reward, rewardShowcase: T) {
        if (showcaseHandle?.showcase?.javaClass == rewardShowcase.javaClass) {
            (showcaseHandle as RewardShowcaseHandle<T>).update(
                rewardShowcase, reward
            )
            return
        }
        showcaseHandle?.destroy()
        val isFirst = showcaseHandle == null
        showcaseHandle = rewardShowcase.create(scenario as CrateAnimation, reward, locationOffset)
        if (isFirst) {
            throwObject(velocity)
        }
    }

    override fun throwObject(vector: Vector) {
        val handle = showcaseHandle ?: return
        if (handle !is ItemRewardShowcaseHandle) return
        val entity = handle.interactable.entity
        val motionPacket = Waves.NMS_HANDLER.createEntityMotionPacket(entity.entityId, vector)
        for (viewer in entity.viewers) {
            viewer.sendPacket(motionPacket)
        }
    }


}