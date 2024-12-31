package gg.aquatic.aquaticcrates.plugin.animation.prop.entity

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.MovableAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathBoundProperties
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathProp
import gg.aquatic.waves.fake.entity.FakeEntity
import gg.aquatic.waves.shadow.com.retrooper.packetevents.protocol.entity.type.EntityType
import gg.aquatic.waves.shadow.com.retrooper.packetevents.protocol.entity.type.EntityTypes
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.util.Vector
import java.util.concurrent.ConcurrentHashMap

class EntityAnimationProp(
    override val animation: Animation,
    override val locationOffset: Vector,
    override val boundPaths: ConcurrentHashMap<PathProp, PathBoundProperties>,
    entityType: String,
    properties: List<EntityProperty>
) : AnimationProp(), MovableAnimationProp {

    var entity: FakeEntity

    override val processedPaths: MutableSet<PathProp> = ConcurrentHashMap.newKeySet()

    init {
        val currentLocation = if (boundPaths.isEmpty()) animation.baseLocation.clone().add(locationOffset)
        else {
            val point = calculatePoint()
            val newLocation = animation.baseLocation.clone().add(point.vector).add(locationOffset)
            newLocation.yaw = point.yaw
            newLocation.pitch = point.pitch

            newLocation
        }

        var peEType: EntityType? = null
        for (value in EntityTypes.values()) {
            if (value.name.key.equals(entityType, ignoreCase = true)) {
                peEType = value
                break
            }
        }
        Bukkit.broadcastMessage("Properties: ${properties.size}")
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
}