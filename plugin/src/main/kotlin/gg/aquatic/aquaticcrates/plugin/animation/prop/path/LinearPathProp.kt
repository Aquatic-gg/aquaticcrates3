package gg.aquatic.aquaticcrates.plugin.animation.prop.path

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.MovableAnimationProp
import org.bukkit.Location
import java.util.TreeMap

class LinearPathProp(
    override val points: TreeMap<Int, PathPoint>,
    override val animation: Animation
): AnimationProp(), PathProp {

    val boundProps = mutableListOf<MovableAnimationProp>()

    var tick = 0
    override var location: Location? = null
        private set

    init {
        tick()
    }

    override fun tick() {
        if (tick > points.lastKey()) return

        val lowerPoint = lowerPoint()
        if (lowerPoint == null) {
            tick++
            return
        }
        val upperPoint = points.higherEntry(tick).toPair()

        if (upperPoint.second == lowerPoint.second) return

        val duration = upperPoint.first - lowerPoint.first
        val currentTick = tick - lowerPoint.first

        val ratio = currentTick.toDouble() / duration.toDouble()

        val interpolatedX = interpolate(lowerPoint.second.x, upperPoint.second.x, ratio)
        val interpolatedY = interpolate(lowerPoint.second.y, upperPoint.second.y, ratio)
        val interpolatedZ = interpolate(lowerPoint.second.z, upperPoint.second.z, ratio)
        val interpolatedYaw = interpolate(lowerPoint.second.yaw.toDouble(), upperPoint.second.yaw.toDouble(), ratio).toFloat()
        val interpolatedPitch = interpolate(lowerPoint.second.pitch.toDouble(), upperPoint.second.pitch.toDouble(), ratio).toFloat()

        val location = animation.baseLocation.clone()
            .add(interpolatedX, interpolatedY, interpolatedZ)
        location.yaw = interpolatedYaw
        location.pitch = interpolatedPitch

        this.location = location

        for (boundProp in boundProps) {
            boundProp.move(location.clone())
        }
        tick++
    }

    override fun onAnimationEnd() {
        tick = 0
        location = null
    }

    private fun lowerPoint(): Pair<Int, PathPoint>? {
        val point = points[tick]
        if (point != null) {
            return Pair(tick, point)
        }
        return points.lowerEntry(tick)?.toPair()
    }

    private fun interpolate(start: Double, end: Double, ratio: Double): Double {
        return start + (end - start) * ratio
    }
}