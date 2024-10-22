package gg.aquatic.aquaticcrates.plugin.pouch

import gg.aquatic.aquaticcrates.api.animation.pouch.PouchAnimationManager
import gg.aquatic.aquaticcrates.api.openprice.OpenPriceGroup
import gg.aquatic.aquaticcrates.api.pouch.Pouch
import gg.aquatic.aquaticcrates.api.reward.RewardAmountRange
import gg.aquatic.aquaticcrates.api.pouch.PouchInteractHandler
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticseries.lib.item2.AquaticItem
import gg.aquatic.aquaticseries.lib.requirement.ConfiguredRequirement
import org.bukkit.entity.Player

class RewardPouch(
    identifier: String,
    item: AquaticItem,
    override val displayName: String,
    override val openRequirements: MutableList<ConfiguredRequirement<Player>>,
    override val openPriceGroups: MutableList<OpenPriceGroup>,
    animationManager: (RewardPouch) -> PouchAnimationManager,
    override val interactHandler: PouchInteractHandler,
    override val rewards: HashMap<String,Pair<Reward,MutableList<RewardAmountRange>>>
) : Pouch(identifier, item) {

    override fun canBeOpened(player: Player): Boolean {
        return true
    }

    override val animationManager = animationManager(this)


}