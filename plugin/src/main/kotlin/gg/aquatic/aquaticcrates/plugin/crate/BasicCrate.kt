package gg.aquatic.aquaticcrates.plugin.crate

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationManager
import gg.aquatic.aquaticcrates.api.crate.CrateInteractHandler
import gg.aquatic.aquaticcrates.api.crate.Key
import gg.aquatic.aquaticcrates.api.crate.OpenableCrate
import gg.aquatic.aquaticcrates.api.hologram.HologramSettings
import gg.aquatic.aquaticcrates.api.milestone.MilestoneManager
import gg.aquatic.aquaticcrates.api.openprice.OpenPriceGroup
import gg.aquatic.aquaticcrates.api.reroll.RerollManager
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.api.reward.RewardAmountRange
import gg.aquatic.aquaticcrates.plugin.crate.interact.BasicInteractHandler
import gg.aquatic.aquaticseries.lib.interactable2.AbstractInteractable
import gg.aquatic.aquaticseries.lib.requirement.ConfiguredRequirement
import org.bukkit.entity.Player

class BasicCrate(
    override val identifier: String,
    override val displayName: String,
    override val hologramSettings: HologramSettings,
    override val interactable: AbstractInteractable<*>,
    override val rewards: HashMap<String, Reward>,
    override val openRequirements: MutableList<ConfiguredRequirement<Player>>,
    override val skipAnimationWhileSneaking: Boolean,
    override val openPriceGroups: MutableList<OpenPriceGroup>,
    animationManager: (BasicCrate) -> CrateAnimationManager,
    val milestoneManager: MilestoneManager,
    rerollManager: (BasicCrate) -> RerollManager,
    key: (BasicCrate) -> Key,
    val rewardRandomAmountRanges: MutableList<RewardAmountRange>
) : OpenableCrate() {

    val animationManager = animationManager(this)
    val rerollManager = rerollManager(this)

    override val key = key(this)
    override fun canBeOpened(player: Player): Boolean {
        TODO("Not yet implemented")
    }

    override var interactHandler: CrateInteractHandler = BasicInteractHandler(this)

}