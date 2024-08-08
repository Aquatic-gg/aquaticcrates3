package gg.aquatic.aquaticcrates.plugin.crate.visual.meg

import com.ticxo.modelengine.api.ModelEngineAPI
import gg.aquatic.aquaticcrates.api.crate.Crate
import gg.aquatic.aquaticcrates.api.crate.visual.CrateVisual
import gg.aquatic.aquaticcrates.api.crate.visual.VisualHandler
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.UUID

class MEGVisual(
    override val location: Location,
    val model: String,
    val skin: Player?,
    override val crate: Crate,
) : CrateVisual() {

    val spawned = HashMap<UUID, UUID>()

    override fun spawn(player: Player) {
        despawn(player)

        val dummy = CrateMEGDummy(this)
        dummy.location = location

        dummy.isDetectingPlayers = false
        dummy.setForceViewing(player, true)

        val modeledEntity = ModelEngineAPI.createModeledEntity(dummy)
        val activeModel = ModelEngineAPI.createActiveModel(model)

        modeledEntity.addModel(activeModel, true)

        spawned += player.uniqueId to dummy.uuid
    }

    override fun despawn(player: Player) {
        val uuid = spawned[player.uniqueId] ?: return

        val modeledEntity = ModelEngineAPI.getModeledEntity(uuid)
        val model = modeledEntity.models.values.firstOrNull() ?: return

        model.destroy()
        modeledEntity.destroy()

        ModelEngineAPI.removeModeledEntity(uuid)
    }

    override fun handler(): MEGVisualHandler {
        return HANDLER
    }

    companion object {

        val HANDLER = MEGVisualHandler()

    }
}