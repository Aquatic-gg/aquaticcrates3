package gg.aquatic.aquaticcrates.plugin.animation.prop.model

import com.ticxo.modelengine.api.entity.Dummy
import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.MovableAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathBoundProperties
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathProp
import gg.aquatic.waves.interactable.type.MEGInteractable
import gg.aquatic.waves.util.runSync
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.concurrent.ConcurrentHashMap

class ModelAnimationProp(
    override val animation: Animation,
    val model: String,
    val skin: Player?,
    val modelAnimation: String?,
    override val locationOffset: Vector,
    override val boundPaths: ConcurrentHashMap<PathProp, Pair<PathBoundProperties, Int>>,
    override val locationOffsetYawPitch: Pair<Float, Float>
) : AnimationProp(), MovableAnimationProp {

    override val processedPaths: MutableSet<PathProp> = ConcurrentHashMap.newKeySet()
    var interactable: MEGInteractable? = null
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
            interactable = MEGInteractable(
                currentLocation,
                model,
                animation.audience,
            ) {}
            skin?.let { interactable!!.setSkin(it) }
            if (modelAnimation != null) {
                playAnimation(modelAnimation)
            }
        }
    }

    override fun tick() {

    }

    fun playAnimation(animation: String, fadeIn: Double = 0.0, fadeOut: Double = 0.0, speed: Double = 1.0) {
        runSync {
            interactable?.activeModel?.animationHandler?.playAnimation(animation, fadeIn, fadeOut, speed, true)
        }
    }

    override fun onAnimationEnd() {
        runSync {
            interactable?.destroy()
        }
    }


    override fun move(location: Location) {
        runSync {
            val dummy = interactable?.modeledEntity?.base as? Dummy<*> ?: return@runSync
            dummy.location = location
            dummy.bodyRotationController.yBodyRot = location.yaw
            dummy.bodyRotationController.xHeadRot = location.pitch
            dummy.bodyRotationController.yHeadRot = location.yaw
            dummy.yHeadRot = location.yaw
            dummy.yBodyRot = location.yaw
        }
    }
}