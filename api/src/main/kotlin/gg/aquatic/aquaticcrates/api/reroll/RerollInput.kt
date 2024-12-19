package gg.aquatic.aquaticcrates.api.reroll

import gg.aquatic.aquaticcrates.api.reward.Reward
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

interface RerollInput {

    fun handle(rerollManager: RerollManager, player: Player, rewards: List<Reward>): CompletableFuture<RerollManager.RerollResult>

}