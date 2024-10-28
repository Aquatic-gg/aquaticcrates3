package gg.aquatic.aquaticcrates.plugin.animation.prop.model

import com.ticxo.modelengine.api.entity.Dummy
import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.MovableAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathBoundProperties
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathProp
import gg.aquatic.aquaticseries.lib.block.AquaticMultiBlock
import gg.aquatic.aquaticseries.lib.block.BlockShape
import gg.aquatic.aquaticseries.lib.interactable2.base.TempInteractableBase
import gg.aquatic.aquaticseries.lib.interactable2.impl.meg.MegInteractable
import gg.aquatic.aquaticseries.lib.interactable2.impl.meg.SpawnedPacketMegInteractable
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.*
import kotlin.collections.HashMap

class ModelAnimationProp(
    override val animation: Animation,
    val model: String,
    val skin: Player?,
    val modelAnimation: String?,
    override val locationOffset: Vector,
    override val boundPaths: MutableMap<PathProp, PathBoundProperties>
) : AnimationProp(), MovableAnimationProp {

    override val processedPaths: MutableList<PathProp> = ArrayList()
    val interactable: SpawnedPacketMegInteractable

    init {
        val currentLocation = if (boundPaths.isEmpty()) animation.baseLocation.clone().add(locationOffset)
        else {
            val point = calculatePoint()
            val newLocation = animation.baseLocation.clone().add(point.vector).add(locationOffset)
            newLocation.yaw = point.yaw
            newLocation.pitch = point.pitch

            newLocation
        }

        interactable = (MegInteractable(
            TempInteractableBase(),
            "animation_${UUID.randomUUID()}",
            AquaticMultiBlock(
                BlockShape(
                    HashMap(),
                    HashMap()
                )
            ),
            model
        ) { _, _ ->

        }.spawnPacket(
            currentLocation,
            animation.audience, register = false, canInteract = false
        ) as SpawnedPacketMegInteractable).apply {
            if (skin != null) {
                setSkin(skin)
            }
        }
        if (modelAnimation != null) {
            playAnimation(modelAnimation)
        }
    }

    override fun tick() {

    }

    fun playAnimation(animation: String, fadeIn: Double = 0.0, fadeOut: Double = 0.0, speed: Double = 1.0) {
        interactable.activeModel!!.animationHandler.playAnimation(animation,fadeIn,fadeOut,speed, true)
    }

    override fun onAnimationEnd() {
        interactable.despawn()
    }


    override fun move(location: Location) {
        val dummy = interactable.modeledEntity!!.base as Dummy<*>
        dummy.location = location
        dummy.bodyRotationController.yBodyRot = location.yaw
        dummy.bodyRotationController.xHeadRot = location.pitch
        dummy.bodyRotationController.yHeadRot = location.yaw
        dummy.yHeadRot = location.yaw
        dummy.yBodyRot = location.yaw
    }
}