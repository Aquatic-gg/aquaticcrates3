package gg.aquatic.aquaticcrates.api.crate

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationManager
import gg.aquatic.aquaticcrates.api.openprice.OpenPriceGroup
import gg.aquatic.aquaticcrates.api.reward.showcase.RewardShowcase
import gg.aquatic.aquaticcrates.api.util.Rewardable
import org.bukkit.entity.Player

abstract class OpenableCrate : Crate(), Rewardable {

    abstract val key: Key
    //abstract val openRequirements: MutableList<ConfiguredRequirement<Player>>
    abstract val openPriceGroups: MutableList<OpenPriceGroup>
    abstract val animationManager: CrateAnimationManager
    abstract val defaultRewardShowcase: RewardShowcase?

    abstract suspend fun tryInstantOpen(player: Player, location: org.bukkit.Location, spawnedCrate: SpawnedCrate?)
    abstract suspend fun instantOpen(player: Player, location: org.bukkit.Location, spawnedCrate: SpawnedCrate?)
    abstract suspend fun tryOpen(player: Player, location: org.bukkit.Location, spawnedCrate: SpawnedCrate?)
    abstract suspend fun open(player: Player, location: org.bukkit.Location, spawnedCrate: SpawnedCrate?)
    abstract suspend fun tryMassOpen(player: Player, amount: Int)
    abstract suspend fun massOpen(player: Player, amount: Int)

    abstract fun openPreview(player: Player, placedCrate: SpawnedCrate?)
}