package gg.aquatic.aquaticcrates.plugin.animation.prop.showcase

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.api.reward.showcase.RewardShowcase
import gg.aquatic.aquaticcrates.api.reward.showcase.RewardShowcaseHandle
import gg.aquatic.aquaticcrates.api.reward.showcase.item.ItemRewardShowcaseHandle
import gg.aquatic.aquaticcrates.plugin.animation.prop.Throwable
import gg.aquatic.waves.Waves
import gg.aquatic.waves.util.sendPacket
import org.bukkit.util.Vector

class RewardShowcaseAnimationProp(
    override val animation: Animation,
    val locationOffset: Pair<Vector, Pair<Float, Float>>,
    val velocity: Vector,
) : AnimationProp(), Throwable {

    var showcaseHandle: RewardShowcaseHandle<*>? = null

    override fun tick() {
    }

    override fun onAnimationEnd() {
        showcaseHandle?.destroy()
        showcaseHandle = null
    }

    fun <T : RewardShowcase> update(reward: Reward, rewardShowcase: T) {
        if (showcaseHandle?.javaClass?.genericInterfaces?.firstOrNull() == rewardShowcase.javaClass) {
            (showcaseHandle as RewardShowcaseHandle<T>).update(
                rewardShowcase, reward
            )
            return
        }
        showcaseHandle?.destroy()
        val isFirst = showcaseHandle == null
        showcaseHandle = rewardShowcase.create(animation, reward, locationOffset)
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