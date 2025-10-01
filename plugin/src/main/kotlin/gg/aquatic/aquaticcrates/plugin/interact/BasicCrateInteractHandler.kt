package gg.aquatic.aquaticcrates.plugin.interact

import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.interaction.crate.CrateInteractHandler
import gg.aquatic.aquaticcrates.api.crate.SpawnedCrate
import gg.aquatic.aquaticcrates.api.event.CrateInteractEvent
import gg.aquatic.aquaticcrates.api.interaction.CrateInteractAction
import gg.aquatic.waves.api.event.call
import gg.aquatic.waves.item.AquaticItemInteractEvent
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import gg.aquatic.waves.util.task.BukkitScope
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.EnumMap

class BasicCrateInteractHandler(
    override val crate: Crate,
    override val clickActions: EnumMap<AquaticItemInteractEvent.InteractType, ConfiguredExecutableObject<CrateInteractAction, Unit>>
) : CrateInteractHandler() {

    override fun handleInteract(
        player: Player,
        interactType: AquaticItemInteractEvent.InteractType,
        interactedLocation: Location,
        crate: SpawnedCrate?
    ): Boolean {

        if (Bukkit.isPrimaryThread()) {
            CrateInteractEvent(
                player,
                crate,
                interactType,
                interactedLocation
            ).call()
        } else {
            BukkitScope.launch {
                CrateInteractEvent(
                    player,
                    crate,
                    interactType,
                    interactedLocation
                ).call()
            }
        }

        val action = clickActions[interactType] ?: return false
        action.execute(
            CrateInteractAction(
                this.crate,
                player,
                interactType,
                interactedLocation,
                crate
            )
        ) { _, str -> str }
        return true
    }

}