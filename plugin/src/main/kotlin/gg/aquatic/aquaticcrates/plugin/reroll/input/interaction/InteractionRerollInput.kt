package gg.aquatic.aquaticcrates.plugin.reroll.input.interaction

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.reroll.RerollInput
import gg.aquatic.aquaticcrates.api.reroll.RerollManager
import gg.aquatic.aquaticcrates.api.reward.RolledReward
import gg.aquatic.aquaticcrates.plugin.reroll.input.InputSettingsFactory
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.CompletableFuture

class InteractionRerollInput(
    val interactions: Map<InteractionType,Action>
): RerollInput {


    override fun handle(
        rerollManager: RerollManager,
        animation: CrateAnimation,
        player: Player,
        rewards: Collection<RolledReward>
    ): CompletableFuture<RerollManager.RerollResult> {
        val future = CompletableFuture<RerollManager.RerollResult>()
        InteractionInputHandler.awaiting += player.uniqueId to (future to interactions)
        return future
    }

    enum class Action {
        REROLL,
        CLAIM
    }

    enum class InteractionType {
        RIGHT_CLICK,
        LEFT_CLICK,
        SNEAK
    }

    companion object: InputSettingsFactory {
        override fun serialize(cfg: FileConfiguration): RerollInput? {
            val actionsSection = cfg.getConfigurationSection("reroll.interaction") ?: return null

            val actions = mutableMapOf<InteractionType,Action>()
            for (key in actionsSection.getKeys(false)) {
                val type = InteractionType.valueOf(key.uppercase())
                val action = Action.valueOf(actionsSection.getString(key)!!.uppercase())
                actions[type] = action
            }
            return InteractionRerollInput(actions)
        }
    }
}