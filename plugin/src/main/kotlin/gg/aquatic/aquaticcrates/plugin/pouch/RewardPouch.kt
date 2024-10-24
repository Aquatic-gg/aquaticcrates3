package gg.aquatic.aquaticcrates.plugin.pouch

import gg.aquatic.aquaticcrates.api.animation.pouch.PouchAnimationManager
import gg.aquatic.aquaticcrates.api.milestone.MilestoneManager
import gg.aquatic.aquaticcrates.api.openprice.OpenPriceGroup
import gg.aquatic.aquaticcrates.api.pouch.Pouch
import gg.aquatic.aquaticcrates.api.reward.RewardAmountRange
import gg.aquatic.aquaticcrates.api.pouch.PouchInteractHandler
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.aquaticcrates.plugin.reward.RolledRewardImpl
import gg.aquatic.aquaticseries.lib.item2.AquaticItem
import gg.aquatic.aquaticseries.lib.requirement.ConfiguredRequirement
import gg.aquatic.aquaticseries.lib.util.randomItem
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class RewardPouch(
    identifier: String,
    item: AquaticItem,
    override val displayName: String,
    override val openRequirements: MutableList<ConfiguredRequirement<Player>>,
    override val openPriceGroups: MutableList<OpenPriceGroup>,
    animationManager: (RewardPouch) -> PouchAnimationManager,
    interactHandler: (RewardPouch) -> PouchInteractHandler,
    override val rewards: HashMap<String, Reward>,
    override val possibleRewardRanges: MutableList<RewardAmountRange>,
    val previewMenuSettings: PouchPreviewMenuSettings, override val milestoneManager: MilestoneManager
) : Pouch(identifier, item) {

    override fun canBeOpened(player: Player): Boolean {
        return true
    }

    override var interactHandler = interactHandler(this)
    override fun generateRewards(player: Player): MutableList<RolledReward> {
        val rolledRewards = mutableListOf<RolledReward>()

        val rewardsAmount = possibleRewardRanges.randomItem()?.randomNum ?: 1
        val randomRewards = getRandomRewards(player, rewardsAmount)
        for ((_, pair) in randomRewards) {
            val reward = pair.first
            val amount = pair.second
            for (i in 0..< amount) {
                rolledRewards += RolledRewardImpl(reward,reward.amountRanges.randomItem()?.randomNum ?: 1)
            }
        }
        return rolledRewards
    }

    fun openPreview(player: Player) {
        if (previewMenuSettings.invSettings == null) return
        PouchPreviewMenu(player, this).open()
    }

    override val animationManager = animationManager(this)


}