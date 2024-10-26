package gg.aquatic.aquaticcrates.plugin.animation.prop.entity

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.MovableAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathProp
import gg.aquatic.aquaticseries.lib.AquaticSeriesLib
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.util.Vector

class EntityAnimationProp(
    override val animation: Animation,
    val locationOffset: Vector,
    val boundLocationOffset: Vector? = null,
    boundPath: PathProp? = null,
    val entityType: String,
    properties: List<EntityProperty>
) : AnimationProp(), MovableAnimationProp {

    val entityId: Int
    val entity: Entity


    init {
        var currentLocation = boundPath?.location?.clone()
        if (currentLocation == null) {
            currentLocation = animation.baseLocation.clone()
            currentLocation.add(locationOffset)
        } else if (boundLocationOffset != null) {
            currentLocation.add(boundLocationOffset)
        }

        entityId = AquaticSeriesLib.INSTANCE.nmsAdapter!!.spawnEntity(
            currentLocation, entityType, animation.audience
        ) {
            for (property in properties) {
                property.apply(it)
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