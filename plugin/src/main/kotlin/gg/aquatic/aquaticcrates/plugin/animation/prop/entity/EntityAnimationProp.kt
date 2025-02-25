package gg.aquatic.aquaticcrates.plugin.animation.prop.entity

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.Moveable
import gg.aquatic.aquaticcrates.plugin.animation.prop.Seatable
import gg.aquatic.aquaticcrates.plugin.animation.prop.Throwable
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathBoundProperties
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathProp
import gg.aquatic.waves.fake.entity.FakeEntity
import gg.aquatic.waves.shadow.com.retrooper.packetevents.protocol.entity.type.EntityType
import gg.aquatic.waves.shadow.com.retrooper.packetevents.protocol.entity.type.EntityTypes
import gg.aquatic.waves.shadow.com.retrooper.packetevents.util.Vector3d
import gg.aquatic.waves.shadow.com.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityVelocity
import gg.aquatic.waves.util.toUser
import org.bukkit.Location
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

        var peEType: EntityType? = null
        for (value in EntityTypes.values()) {
            if (value.name.key.equals(entityType, ignoreCase = true)) {
                peEType = value
                break
            }
        }
        entity = FakeEntity(peEType!!, currentLocation, 50, animation.audience)
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
        val packet = WrapperPlayServerEntityVelocity(entity.entityId, Vector3d(vector.x, vector.y, vector.z))
        for (viewer in entity.viewers) {
            viewer.toUser()?.sendPacket(packet)
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