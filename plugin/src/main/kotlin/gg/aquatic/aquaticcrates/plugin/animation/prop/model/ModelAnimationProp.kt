package gg.aquatic.aquaticcrates.plugin.animation.prop.model

import com.ticxo.modelengine.api.entity.Dummy
import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.MovableAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathBoundProperties
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathProp
import gg.aquatic.waves.interactable.type.MEGInteractable
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
    override val boundPaths: ConcurrentHashMap<PathProp, PathBoundProperties>
) : AnimationProp(), MovableAnimationProp {

    override val processedPaths: MutableSet<PathProp> = ConcurrentHashMap.newKeySet()
    val interactable: MEGInteractable

    init {
        val currentLocation = if (boundPaths.isEmpty()) animation.baseLocation.clone().add(locationOffset)
        else {
            val point = calculatePoint()
            val newLocation = animation.baseLocation.clone().add(point.vector).add(locationOffset)
            newLocation.yaw = point.yaw
            newLocation.pitch = point.pitch

            newLocation
        }

        interactable = MEGInteractable(
            currentLocation,
            model,
            animation.audience,
        ) {}
        skin?.let { interactable.setSkin(it) }
        if (modelAnimation != null) {
            playAnimation(modelAnimation)
        }
    }

    override fun tick() {

    }

    fun playAnimation(animation: String, fadeIn: Double = 0.0, fadeOut: Double = 0.0, speed: Double = 1.0) {
        interactable.activeModel.animationHandler.playAnimation(animation,fadeIn,fadeOut,speed, true)
    }

    override fun onAnimationEnd() {
        interactable.destroy()
    }


    override fun move(location: Location) {
        val dummy = interactable.modeledEntity.base as Dummy<*>
        dummy.location = location
        dummy.bodyRotationController.yBodyRot = location.yaw
        dummy.bodyRotationController.xHeadRot = location.pitch
        dummy.bodyRotationController.yHeadRot = location.yaw
        dummy.yHeadRot = location.yaw
        dummy.yBodyRot = location.yaw
    }
}