package gg.aquatic.aquaticcrates.plugin.animation.idle.settings

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate
import gg.aquatic.aquaticcrates.plugin.animation.idle.IdleAnimationImpl
import gg.aquatic.waves.util.chance.IChance
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import java.util.*

class IdleAnimationSettings(
    val animationTasks: TreeMap<Int, ConfiguredExecutableObject<Animation, Unit>>,
    val length: Int,
    val isLoop: Boolean,
    override val chance: Double
) : IChance {

    fun create(spawnedCrate: SpawnedCrate): IdleAnimationImpl {
        return IdleAnimationImpl(
            spawnedCrate,
            spawnedCrate.location,
            this
        )
    }

}