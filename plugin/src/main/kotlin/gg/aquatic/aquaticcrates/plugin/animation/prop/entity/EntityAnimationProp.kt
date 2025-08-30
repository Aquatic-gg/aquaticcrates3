package gg.aquatic.aquaticcrates.plugin.animation.prop.entity

import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.waves.Waves
import gg.aquatic.waves.fake.entity.FakeEntity
import gg.aquatic.waves.scenario.Scenario
import gg.aquatic.waves.scenario.ScenarioProp
import gg.aquatic.waves.scenario.prop.Moveable
import gg.aquatic.waves.scenario.prop.Seatable
import gg.aquatic.waves.scenario.prop.Throwable
import gg.aquatic.waves.scenario.prop.path.PathBoundProperties
import gg.aquatic.waves.scenario.prop.path.PathProp
import gg.aquatic.waves.util.sendPacket
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.util.Vector
import java.util.concurrent.ConcurrentHashMap

class EntityAnimationProp(
    override val scenario: Scenario,
    override val locationOffset: Vector,
    override val boundPaths: ConcurrentHashMap<PathProp, Pair<PathBoundProperties, Int>>,
    entityType: String,
    properties: Collection<EntityProperty>,
    override val locationOffsetYawPitch: Pair<Float, Float>
) : ScenarioProp, Moveable, Throwable, Seatable {

    var entity: FakeEntity

    override val processedPaths: MutableSet<PathProp> = ConcurrentHashMap.newKeySet()

    init {
        val currentLocation = if (boundPaths.isEmpty()) scenario.baseLocation.clone().add(locationOffset).apply {
            yaw += locationOffsetYawPitch.first
            pitch += locationOffsetYawPitch.second
        }
        else {
            val point = calculatePoint()
            val newLocation = scenario.baseLocation.clone().add(point.vector).add(locationOffset)
            newLocation.yaw = point.yaw + locationOffsetYawPitch.first
            newLocation.pitch = point.pitch + locationOffsetYawPitch.second

            newLocation
        }

        entity = FakeEntity(EntityType.valueOf(entityType.uppercase()), currentLocation, 50, scenario.audience)
        for (property in properties) {
            property.apply(entity, this@EntityAnimationProp)
        }
    }

    override fun tick() {

    }

    override fun onEnd() {
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

    override fun addPassenger(entityAnimationProp: ScenarioProp) {
        if (entityAnimationProp !is EntityAnimationProp) return
        entity.updateEntity {
            passengers += entityAnimationProp.entity.entityId
        }
    }

    override fun removePassenger(entityAnimationProp: ScenarioProp) {
        if (entityAnimationProp !is EntityAnimationProp) return
        entity.updateEntity {
            passengers -= entityAnimationProp.entity.entityId
        }
    }
}