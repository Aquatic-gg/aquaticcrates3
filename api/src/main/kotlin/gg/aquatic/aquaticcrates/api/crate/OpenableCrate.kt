package gg.aquatic.aquaticcrates.api.crate

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationManager
import gg.aquatic.aquaticcrates.api.openprice.OpenPriceGroup
import gg.aquatic.aquaticcrates.api.util.Rewardable
import gg.aquatic.waves.util.requirement.ConfiguredRequirement
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

abstract class OpenableCrate : Crate(), Rewardable {

    abstract val key: Key
    abstract val openRequirements: MutableList<ConfiguredRequirement<Player>>
    abstract val openPriceGroups: MutableList<OpenPriceGroup>
    abstract val animationManager: CrateAnimationManager

    abstract fun tryInstantOpen(player: Player, location: org.bukkit.Location, spawnedCrate: SpawnedCrate?)
    abstract fun instantOpen(player: Player, location: org.bukkit.Location, spawnedCrate: SpawnedCrate?)
    abstract fun tryOpen(player: Player, location: org.bukkit.Location, spawnedCrate: SpawnedCrate?): CompletableFuture<Void>
    abstract fun open(player: Player, location: org.bukkit.Location, spawnedCrate: SpawnedCrate?): CompletableFuture<Void>
    abstract fun tryMassOpen(player: Player, amount: Int, threads: Int?): CompletableFuture<Void>
    abstract fun massOpen(player: Player, amount: Int, threads: Int?): CompletableFuture<Void>

    abstract fun openPreview(player: Player, placedCrate: SpawnedCrate?)
}