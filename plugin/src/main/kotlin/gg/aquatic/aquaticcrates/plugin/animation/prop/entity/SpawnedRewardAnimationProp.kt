package gg.aquatic.aquaticcrates.plugin.animation.prop.entity

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.waves.fake.entity.FakeEntity
import gg.aquatic.waves.packetevents.EntityDataBuilder
import gg.aquatic.waves.shadow.com.retrooper.packetevents.protocol.entity.type.EntityTypes
import gg.aquatic.waves.shadow.com.retrooper.packetevents.util.Vector3d
import gg.aquatic.waves.shadow.com.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityVelocity
import gg.aquatic.waves.util.mapPair
import gg.aquatic.waves.util.toUser
import org.bukkit.util.Vector

class SpawnedRewardAnimationProp(
    override val animation: CrateAnimation,
    val rumblingLength: Int,
    val rumblingPeriod: Int,
    val aliveLength: Int,
    val offset: Vector,
    val velocity: Vector,
    val gravity: Boolean,
    val easeOut: Boolean,
    val rewardIndex: Int
) : AnimationProp() {

    var tick = -1
        private set

    var finished = false
        private set

    var previousReward: Reward? = null
        private set

    var entity: FakeEntity? = null
        private set

    override fun tick() {
        tick++
        if (finished) {
            if (entity != null && tick >= aliveLength) {
                entity?.destroy()
                entity = null
            }
            return
        }
        if (tick >= rumblingLength) {
            updateItem(animation.rewards.getOrNull(rewardIndex)?.reward ?: animation.rewards.first().reward)
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
            if (previousReward != null) {
                rewards.remove(previousReward)
            }
            previousReward = rewards.random()
            updateItem(previousReward!!)
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

    private fun updateItem(reward: Reward) {
        if (entity == null) {
            spawnItem(reward)
            return
        }

        entity?.updateEntity {
            val builder = EntityDataBuilder.ANY
            builder.hasNoGravity(!gravity)
            val data = EntityDataBuilder.ITEM.setItem(reward.item.getItem()).build().toMutableList()
            data += builder.build()

            this.entityData += data.mapPair { it.index to it }
        }
    }

    private fun spawnItem(reward: Reward) {
        val location = animation.baseLocation.clone().add(0.0,1.0,0.0).add(offset)
        if (!location.chunk.isLoaded) {
            location.chunk.load()
        }

        val builder = EntityDataBuilder.ANY
        builder.hasNoGravity(!gravity)
        val data = EntityDataBuilder.ITEM.setItem(reward.item.getItem()).build().toMutableList()
        data += builder.build()

        val entity = FakeEntity(EntityTypes.ITEM, location, 50, animation.audience)
        entity.updateEntity {
            this.entityData += data.mapPair { it.index to it }
        }

        entity.tick()
        entity.show(animation.player)

        val throwPacket = WrapperPlayServerEntityVelocity(entity.entityId, Vector3d(velocity.x, velocity.y, velocity.z))
        for (viewer in entity.viewers) {
            viewer.toUser().sendPacket(throwPacket)
        }
    }

    override fun onAnimationEnd() {
        entity?.destroy()
    }
}