package gg.aquatic.aquaticcrates.plugin.crate

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationManager
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationSettings
import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import gg.aquatic.aquaticcrates.api.crate.Key
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate
import gg.aquatic.aquaticcrates.api.hologram.HologramSettings
import gg.aquatic.aquaticcrates.api.interaction.crate.CrateInteractHandler
import gg.aquatic.aquaticcrates.api.openprice.OpenPriceGroup
import gg.aquatic.aquaticcrates.api.reward.RewardManager
import gg.aquatic.aquaticcrates.plugin.CratesPlugin
import gg.aquatic.aquaticcrates.plugin.preview.CratePreviewMenuSettings
import gg.aquatic.aquaticcrates.plugin.restriction.OpenData
import gg.aquatic.aquaticcrates.plugin.takeKeys
import gg.aquatic.waves.interactable.settings.InteractableSettings
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.item.AquaticItemInteractEvent
import gg.aquatic.waves.registry.register
import gg.aquatic.waves.registry.setInteractionHandler
import gg.aquatic.waves.util.collection.checkRequirements
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import gg.aquatic.waves.util.item.modifyFastMeta
import gg.aquatic.waves.util.requirement.ConfiguredRequirement
import gg.aquatic.waves.util.runLaterSync
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.runAsync

class BasicCrate(
    override val identifier: String,
    override val displayName: String,
    override val hologramSettings: HologramSettings,
    override val interactables: List<InteractableSettings>,
    override val openRequirements: MutableList<ConfiguredRequirement<Player>>,
    override val openPriceGroups: MutableList<OpenPriceGroup>,
    animationManager: (BasicCrate) -> CrateAnimationManager,
    key: (BasicCrate) -> Key,
    rewardManager: (BasicCrate) -> RewardManager,
    interactHandler: (BasicCrate) -> CrateInteractHandler,
    val previewMenuSettings: MutableList<CratePreviewMenuSettings>,
    val massOpenFinalActions: MutableList<ConfiguredExecutableObject<Player,Unit>>,
    val massOpenPerRewardActions: MutableList<ConfiguredExecutableObject<Player,Unit>>,
    val openRestrictions: MutableList<ConfiguredRequirement<OpenData>>
) : OpenableCrate() {

    var openManager = BasicOpenManager(this)

    override var interactHandler: CrateInteractHandler = interactHandler(this)
    override val rewardManager: RewardManager = rewardManager(this)
    override val animationManager = animationManager(this)
    override val key = key(this)

    val crateItem = AquaticItem(
        ItemStack(Material.CHEST).apply {
            modifyFastMeta {
                displayName = Component.text("Crate: $identifier").decoration(TextDecoration.ITALIC,false)
            }
        },
        null,
        null,
        1,
        -1,
        null,
        null,
        null
    ).apply {
        val consumer: (AquaticItemInteractEvent) -> Unit = { e->
            val originalEvent = e.originalEvent
            if (originalEvent !is InventoryClickEvent) {
                e.isCancelled = true
                if (originalEvent is PlayerInteractEvent) {
                    val location = originalEvent.clickedBlock?.location ?: originalEvent.player.location
                    location.yaw = originalEvent.player.location.yaw - 180
                    if (e.interactType == AquaticItemInteractEvent.InteractType.RIGHT) {
                        runLaterSync(2) {
                            CrateHandler.spawnCrate(this@BasicCrate, location.clone().add(.5, 1.0, .5))
                            runAsync {
                                CrateHandler.saveSpawnedCrates(CratesPlugin.spawnedCratesConfig)
                            }
                        }
                        e.player.sendMessage("Crate Spawned")
                    }
                }
            }
        }
        if (!register("aquaticcrates-crates", identifier,consumer)) {
            setInteractionHandler(consumer)
        }
    }
    override fun tryInstantOpen(
        player: Player,
        location: Location,
        spawnedCrate: SpawnedCrate?
    ) {
        if (!canBeOpened(player,1,null)) {
            player.sendMessage("You do not have enough keys to open this crate!")
            return
        }
        instantOpen(player, location, spawnedCrate)
    }

    override fun instantOpen(
        player: Player,
        location: Location,
        spawnedCrate: SpawnedCrate?
    ) {
        openManager.instantOpen(player,false)
    }

    override fun tryOpen(player: Player, location: Location, spawnedCrate: SpawnedCrate?): CompletableFuture<Void> {
        if (!canBeOpened(player,1,OpenData(player,location,this))) {
            spawnedCrate?.let {
                animationManager.playFailAnimation(it, player)
            }
            return CompletableFuture.completedFuture(null)
        }
        return open(player, location, spawnedCrate)
    }

    override fun open(
        player: Player,
        location: Location,
        spawnedCrate: SpawnedCrate?
    ): CompletableFuture<Void> {
        return openManager.open(player, location, spawnedCrate)
    }

    override fun tryMassOpen(player: Player, amount: Int, threads: Int?): CompletableFuture<Void> {
        if (!canBeOpened(player, amount, null)) {
            return CompletableFuture.completedFuture(null)
        }
        return massOpen(
            player,
            amount,
            threads
        )
    }

    override fun massOpen(player: Player, amount: Int, threads: Int?): CompletableFuture<Void> {
        return openManager.massOpen(player, amount, threads)
    }


    fun canBeOpened(player: Player, amount: Int, openData: OpenData?): Boolean {
        if (openData != null) {
            if (!openRestrictions.checkRequirements(openData)) {
                player.sendMessage("You cannot open the crate here!")
                return false
            }
            val spawned = CrateHandler.spawned[openData.location]
            if (spawned != null) {
                if (animationManager.failAnimations[spawned]?.containsKey(player.uniqueId) == true) {
                    return false
                }
            }

            val animationResult = animationManager.animationSettings.canBeOpened(player,animationManager,openData.location)
            when (animationResult) {
                CrateAnimationSettings.AnimationResult.ALREADY_BEING_OPENED -> {
                    player.sendMessage("You are already opening a crate!")
                    return false
                }
                CrateAnimationSettings.AnimationResult.ALREADY_BEING_OPENED_OTHER -> {
                    player.sendMessage("Someone else is already opening a crate!")
                    return false
                }
                else -> {}
            }
        }
        if (!player.takeKeys(identifier, amount)) {
            player.sendMessage("You do not have enough keys to open this crate!")
            return false
        }
        return true
    }

}