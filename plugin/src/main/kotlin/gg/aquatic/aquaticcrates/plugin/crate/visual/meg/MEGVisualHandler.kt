package gg.aquatic.aquaticcrates.plugin.crate.visual.meg

import com.ticxo.modelengine.api.events.BaseEntityInteractEvent
import com.ticxo.modelengine.api.events.BaseEntityInteractEvent.Action
import gg.aquatic.aquaticcrates.plugin.CratesPlugin
import gg.aquatic.aquaticcrates.api.crate.visual.VisualHandler
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.EquipmentSlot

class MEGVisualHandler : VisualHandler() {

    init {
        registerListeners()
    }

    private fun registerListeners() {
        val inst = CratesPlugin.INSTANCE
        Bukkit.getServer().pluginManager.registerEvents(Listeners(), inst)
    }

    class Listeners : Listener {

        @EventHandler
        fun BaseEntityInteractEvent.onBaseInteract() {
            if (this.slot == EquipmentSlot.OFF_HAND) return
            val base = this.baseEntity
            if (base !is CrateMEGDummy) return

            val visual = base.visual

            visual.crate.interactHandler.handleInteract(this.player, this.action == Action.ATTACK)
        }

    }

}