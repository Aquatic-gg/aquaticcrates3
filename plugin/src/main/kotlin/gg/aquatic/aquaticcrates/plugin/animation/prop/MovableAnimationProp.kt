package gg.aquatic.aquaticcrates.plugin.animation.prop

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathBoundProperties
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathPoint
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathProp
import org.bukkit.Location
import org.bukkit.util.Vector
import org.joml.Math.toRadians

interface MovableAnimationProp {

    val animation: Animation

    val boundPaths: MutableMap<PathProp,PathBoundProperties>
    val processedPaths: MutableList<PathProp>

    //val boundLocationOffset: Vector?

    fun processPath(
        path: PathProp,
        point: PathPoint) {

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

    fun calculatePoint(): PathPoint {
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

    fun move(location: Location)

}