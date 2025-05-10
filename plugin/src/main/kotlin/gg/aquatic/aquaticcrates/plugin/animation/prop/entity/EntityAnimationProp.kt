package gg.aquatic.aquaticcrates.plugin.animation.prop.entity

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.Moveable
import gg.aquatic.aquaticcrates.plugin.animation.prop.Seatable
import gg.aquatic.aquaticcrates.plugin.animation.prop.Throwable
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathBoundProperties
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathProp
import gg.aquatic.waves.Waves
import gg.aquatic.waves.fake.entity.FakeEntity
import gg.aquatic.waves.util.sendPacket
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.util.Vector
import java.util.concurrent.ConcurrentHashMap

class EntityAnimationProp(
    override val animation: Animation,
    override val locationOffset: Vector,
    override val boundPaths: ConcurrentHashMap<PathProp, Pair<PathBoundProperties, Int>>,
    entityType: String,
    properties: Collection<EntityProperty>,
    override val locationOffsetYawPitch: Pair<Float, Float>
) : AnimationProp(), Moveable, Throwable, Seatable {

    var entity: FakeEntity

    override val processedPaths: MutableSet<PathProp> = ConcurrentHashMap.newKeySet()

    init {
        val currentLocation = if (boundPaths.isEmpty()) animation.baseLocation.clone().add(locationOffset).apply {
            yaw += locationOffsetYawPitch.first
            pitch += locationOffsetYawPitch.second
        }
        else {
            val point = calculatePoint()
            val newLocation = animation.baseLocation.clone().add(point.vector).add(locationOffset)
            newLocation.yaw = point.yaw + locationOffsetYawPitch.first
            newLocation.pitch = point.pitch + locationOffsetYawPitch.second

            newLocation
        }

        entity = FakeEntity(EntityType.valueOf(entityType.uppercase()), currentLocation, 50, animation.audience)
        for (property in properties) {
            property.apply(entity, this@EntityAnimationProp)
        }
    }

    override fun tick() {

    }

    override fun onAnimationEnd() {
        entity.destroy()
    }

    override fun move(location: Location) {
        entity.teleport(location)
    }

    override fun throwObject(vector: Vector) {
        val motionPacket = Waves.NMS_HANDLER.createEntityMotionPacket(entity.entityId, vector)
        for (viewer in entity.viewers) {
            viewer.sendPacket(motionPacket)
        }
    }

    override fun addPassenger(entityAnimationProp: EntityAnimationProp) {
        entity.updateEntity {
            passengers += entityAnimationProp.entity.entityId
        }
    }

    override fun removePassenger(entityAnimationProp: EntityAnimationProp) {
        entity.updateEntity {
            passengers -= entityAnimationProp.entity.entityId
        }
    }
}