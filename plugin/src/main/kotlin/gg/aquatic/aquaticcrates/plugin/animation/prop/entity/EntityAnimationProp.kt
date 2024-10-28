package gg.aquatic.aquaticcrates.plugin.animation.prop.entity

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.MovableAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathBoundProperties
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathPoint
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathProp
import gg.aquatic.aquaticseries.lib.AquaticSeriesLib
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.util.Vector
import org.joml.Math.toRadians

class EntityAnimationProp(
    override val animation: Animation,
    val locationOffset: Vector,
    override val boundPaths: MutableMap<PathProp, PathBoundProperties>,
    val entityType: String,
    properties: List<EntityProperty>
) : AnimationProp(), MovableAnimationProp {

    val entityId: Int
    val entity: Entity

    override val processedPaths: MutableList<PathProp> = ArrayList()

    init {
        val currentLocation = if (boundPaths.isEmpty()) animation.baseLocation.clone().add(locationOffset)
        else {
            val point = calculatePoint()
            val newLocation = animation.baseLocation.clone().add(point.vector)
            newLocation.yaw = point.yaw
            newLocation.pitch = point.pitch

            newLocation
        }

        entityId = AquaticSeriesLib.INSTANCE.nmsAdapter!!.spawnEntity(
            currentLocation, entityType, animation.audience

        ) {
            for (property in properties) {
                Bukkit.getConsoleSender().sendMessage("Applying property!")
                property.apply(it, this)
            }
        }

        entity = AquaticSeriesLib.INSTANCE.nmsAdapter!!.getEntity(entityId)!!
    }

    override fun tick() {

    }

    override fun onAnimationEnd() {
        AquaticSeriesLib.INSTANCE.nmsAdapter!!.despawnEntity(listOf(entityId), animation.audience)
    }


    override fun processPath(path: PathProp, point: PathPoint) {
        processedPaths += path
        if (processedPaths.size != boundPaths.size) {
            return
        }

        val currentPoint = calculatePoint()

        val newLocation = animation.baseLocation.clone().add(currentPoint.vector)
        newLocation.yaw = currentPoint.yaw
        newLocation.pitch = currentPoint.pitch

        move(newLocation)
        processedPaths.clear()
    }

    private fun calculatePoint(): PathPoint {
        var currentPoint = PathPoint(0.0, 0.0, 0.0, 0f, 0f)
        for ((p, properties) in boundPaths) {
            val po = p.currentPoint
            val offset = properties.offset

            val pointVector = Vector(po.x, po.y, po.z)

            var yaw: Float
            var pitch: Float

            if (properties.affectYawPitch) {
                yaw = currentPoint.yaw + po.yaw
                pitch = currentPoint.pitch + po.pitch
            } else {
                yaw = currentPoint.yaw
                pitch = currentPoint.pitch
            }
            yaw += offset.yaw
            pitch += offset.pitch

            if (properties.offsetType == PathBoundProperties.OffsetType.STATIC) {
                currentPoint = PathPoint(
                    pointVector.x + offset.x + currentPoint.x,
                    pointVector.y + offset.y + currentPoint.y,
                    pointVector.z + offset.z + currentPoint.z,
                    yaw,
                    pitch
                )
            } else {
                val newV = pointVector.clone().add(offset.vector).rotateAroundY(-toRadians(po.yaw).toDouble())
                val x = newV.x + currentPoint.x
                val y = newV.y + currentPoint.y
                val z = newV.z + currentPoint.z
                currentPoint = PathPoint(x, y, z, yaw, pitch)
            }
        }
        return currentPoint
    }

    override fun move(location: Location) {
        AquaticSeriesLib.INSTANCE.nmsAdapter!!.teleportEntity(entityId, location, animation.audience)
    }
}