package gg.aquatic.aquaticcrates.plugin.animation.prop.model

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.Moveable
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathBoundProperties
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathProp
import gg.aquatic.waves.interactable.type.BMInteractable
import gg.aquatic.waves.util.runSync
import kr.toxicity.model.api.animation.AnimationModifier
import org.bukkit.Location
import org.bukkit.util.Vector
import java.util.concurrent.ConcurrentHashMap

class BMModelAnimationProp(
    override val animation: Animation,
    val model: String,
    val modelAnimation: String?,
    override val locationOffset: Vector,
    override val boundPaths: ConcurrentHashMap<PathProp, Pair<PathBoundProperties, Int>>,
    override val locationOffsetYawPitch: Pair<Float, Float>
) : AnimationProp(), Moveable {

    override val processedPaths: MutableSet<PathProp> = ConcurrentHashMap.newKeySet()
    var interactable: BMInteractable? = null
        private set

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

        runSync {
            interactable = BMInteractable(
                currentLocation,
                model,
                animation.audience,
            ) {}
            if (modelAnimation != null) {
                playAnimation(modelAnimation)
            }
        }
    }

    override fun tick() {

    }

    fun playAnimation(animation: String, fadeIn: Int = 0, fadeOut: Int = 0, speed: Float = 1.0f) {
        runSync {
            interactable?.tracker?.animate(animation, AnimationModifier(fadeIn,fadeOut,speed))
        }
    }

    override fun onAnimationEnd() {
        runSync {
            interactable?.destroy()
        }
    }


    override fun move(location: Location) {
        runSync {
            interactable?.tracker?.location(location)
        }
    }
}