package gg.aquatic.aquaticcrates.plugin.reroll

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationActions
import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.reroll.RerollInput
import gg.aquatic.aquaticcrates.api.reroll.RerollManager
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

class RerollManagerImpl(
    override val crate: Crate, override val groups: HashMap<String, Int>, override val rerollInput: RerollInput,
    override val animationTasks: CrateAnimationActions
) : RerollManager() {

    override fun openReroll(
        player: Player,
        animation: CrateAnimation,
        rewards: Collection<RolledReward>
    ): CompletableFuture<RerollResult> {
        return rerollInput.handle(this, animation, player, rewards)
    }
}