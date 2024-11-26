package gg.aquatic.aquaticcrates.plugin.crate

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationManager
import gg.aquatic.aquaticcrates.api.crate.CrateHandler
import gg.aquatic.aquaticcrates.api.interaction.crate.CrateInteractHandler
import gg.aquatic.aquaticcrates.api.crate.Key
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.hologram.HologramSettings
import gg.aquatic.aquaticcrates.api.openprice.OpenPriceGroup
import gg.aquatic.aquaticcrates.api.reward.RewardManager
import gg.aquatic.aquaticcrates.plugin.preview.CratePreviewMenuSettings
import gg.aquatic.aquaticseries.lib.requirement.ConfiguredRequirement
import gg.aquatic.aquaticseries.lib.util.runLaterSync
import gg.aquatic.waves.interactable.settings.InteractableSettings
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.item.modifyFastMeta
import gg.aquatic.waves.registry.register
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

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
    val previewMenuSettings: MutableList<CratePreviewMenuSettings>
) : OpenableCrate() {

    override var interactHandler: CrateInteractHandler = interactHandler(this)
    override val rewardManager: RewardManager = rewardManager(this)
    override val animationManager = animationManager(this)

    val crateItem = AquaticItem(
        ItemStack(
            org.bukkit.Material.CHEST).apply {
                modifyFastMeta {
                    displayName = Component.text("Crate: $identifier")
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
        register("aquaticcrates:crates", identifier) { e->
            e.isCancelled = true
            val originalEvent = e.originalEvent
            val location = if (originalEvent is PlayerInteractEvent) {
                originalEvent.clickedBlock?.location ?: originalEvent.player.location
            } else return@register
            runLaterSync(2) {
                CrateHandler.spawnCrate(this@BasicCrate,location.clone().add(.0,1.0, .0))
            }
            e.player.sendMessage("Crate Spawned")
        }
    }


    override val key = key(this)
    override fun canBeOpened(player: Player): Boolean {
        return true
    }
}