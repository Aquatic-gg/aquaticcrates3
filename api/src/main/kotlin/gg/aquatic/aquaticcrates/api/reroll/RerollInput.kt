package gg.aquatic.aquaticcrates.api.reroll

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

interface RerollInput {

    fun handle(rerollManager: RerollManager, animation: CrateAnimation, player: Player, rewards: Collection<RolledReward>): CompletableFuture<RerollManager.RerollResult>

}