package gg.aquatic.aquaticcrates.plugin.pouch

import gg.aquatic.aquaticcrates.api.animation.pouch.PouchAnimationManager
import gg.aquatic.aquaticcrates.api.milestone.MilestoneManager
import gg.aquatic.aquaticcrates.api.openprice.OpenPriceGroup
import gg.aquatic.aquaticcrates.api.pouch.Pouch
import gg.aquatic.aquaticcrates.api.reward.RewardAmountRange
import gg.aquatic.aquaticcrates.api.pouch.PouchInteractHandler
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.api.reward.RewardManager
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.aquaticcrates.plugin.reward.RolledRewardImpl
import gg.aquatic.aquaticseries.lib.requirement.ConfiguredRequirement
import gg.aquatic.aquaticseries.lib.util.randomItem
import gg.aquatic.waves.item.AquaticItem
import org.bukkit.entity.Player

class RewardPouch(
    identifier: String,
    item: AquaticItem,
    override val displayName: String,
    override val openRequirements: MutableList<ConfiguredRequirement<Player>>,
    override val openPriceGroups: MutableList<OpenPriceGroup>,
    animationManager: (RewardPouch) -> PouchAnimationManager,
    interactHandler: (RewardPouch) -> PouchInteractHandler,
    val previewMenuSettings: PouchPreviewMenuSettings, override val milestoneManager: MilestoneManager,
    override val rewardManager: RewardManager
) : Pouch(identifier, item) {

    override fun canBeOpened(player: Player): Boolean {
        return true
    }

    override var interactHandler = interactHandler(this)
    override fun generateRewards(player: Player): MutableList<RolledReward> {
        val rolledRewards = mutableListOf<RolledReward>()
        return rolledRewards
    }

    fun openPreview(player: Player) {
        if (previewMenuSettings.invSettings == null) return
        PouchPreviewMenu(player, this).open()
    }

    override val animationManager = animationManager(this)


}