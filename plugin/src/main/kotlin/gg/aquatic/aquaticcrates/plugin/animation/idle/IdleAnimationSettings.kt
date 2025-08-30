package gg.aquatic.aquaticcrates.plugin.animation.idle

import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate
import gg.aquatic.waves.scenario.Scenario
import gg.aquatic.waves.util.chance.IChance
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import java.util.*

class IdleAnimationSettings(
    val animationTasks: TreeMap<Int, MutableList<ConfiguredExecutableObject<Scenario, Unit>>>,
    val length: Int,
    val isLoop: Boolean,
    override val chance: Double
) : IChance {

    fun create(spawnedCrate: SpawnedCrate): IdleAnimationImpl {
        return IdleAnimationImpl(
            spawnedCrate,
            this
        )
    }

}