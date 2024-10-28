package gg.aquatic.aquaticcrates.plugin.animation.prop.entity

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.MovableAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathBoundProperties
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathProp
import gg.aquatic.aquaticseries.lib.AquaticSeriesLib
import gg.aquatic.aquaticseries.lib.util.runSync
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.util.Vector

class EntityAnimationProp(
    override val animation: Animation,
    override val locationOffset: Vector,
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
            val newLocation = animation.baseLocation.clone().add(point.vector).add(locationOffset)
            newLocation.yaw = point.yaw
            newLocation.pitch = point.pitch

            newLocation
        }

        entityId = AquaticSeriesLib.INSTANCE.nmsAdapter!!.spawnEntity(
            currentLocation, entityType, animation.audience

        ) {
            for (property in properties) {
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

    override fun move(location: Location) {
        AquaticSeriesLib.INSTANCE.nmsAdapter!!.teleportEntity(entityId, location, animation.audience)
    }
}