package gg.aquatic.aquaticcrates.plugin.reroll.input.inventory

import gg.aquatic.aquaticcrates.api.reroll.RerollInput
import gg.aquatic.aquaticcrates.api.reroll.RerollManager
import gg.aquatic.aquaticcrates.api.reward.Reward
import gg.aquatic.aquaticcrates.plugin.reroll.input.InputSettingsFactory
import gg.aquatic.waves.registry.serializer.InventorySerializer
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

class InventoryRerollInput(
    val settings: RerollInventorySettings
) : RerollInput {
    override fun handle(
        rerollManager: RerollManager,
        player: Player,
        rewards: List<Reward>
    ): CompletableFuture<RerollManager.RerollResult> {
        val future = CompletableFuture<RerollManager.RerollResult>()

        val menu = RerollMenu(
            player,
            rewards,
            settings,
            future
        )
        menu.open()

        return future
    }

    enum class Action {
        REROLL,
        CLAIM,
        CANCEL
    }

    companion object : InputSettingsFactory {
        override fun serialize(cfg: FileConfiguration): RerollInput? {
            val inventorySettings =
                InventorySerializer.loadInventory(cfg.getConfigurationSection("reroll.inventory") ?: return null)
                    ?: return null

            val clearBottomInventory = cfg.getBoolean("reroll.inventory.clear-bottom-inventory")
            val rewardSlots = InventorySerializer.loadSlotSelection(cfg.getStringList("reroll.inventory.reward-slots"))
            val onCloseAction =
                Action.valueOf(cfg.getString("reroll.inventory.on-close-action", "CANCEL")!!.uppercase())
            val settings = RerollInventorySettings(
                inventorySettings,
                rewardSlots,
                clearBottomInventory,
                onCloseAction
            )
            return InventoryRerollInput(
                settings
            )
        }

    }
}