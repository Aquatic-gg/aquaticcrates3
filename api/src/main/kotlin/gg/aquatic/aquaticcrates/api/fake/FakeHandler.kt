package gg.aquatic.aquaticcrates.api.fake

import gg.aquatic.aquaticcrates.api.fake.event.PacketBlockInteractEvent
import gg.aquatic.aquaticcrates.api.fake.event.PacketEntityInteractEvent
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.plugin.java.JavaPlugin

object FakeHandler {


    fun registerBlock(packetBlock: PacketBlock) {
        FakeRegistry.registerBlock(packetBlock)
    }
    fun registerEntity(packetEntity: PacketEntity) {
        FakeRegistry.registerEntity(packetEntity)
    }

    fun unregisterBlock(location: Location) {
        FakeRegistry.unregisterBlock(location)
    }
    fun unregisterEntity(location: Location, id: Int) {
        FakeRegistry.unregisterEntity(location, id)
    }

    fun getBlock(location: Location): PacketBlock? {
        return FakeRegistry.getBlock(location)
    }

    fun registerListeners(plugin: JavaPlugin) {
        plugin.server.pluginManager.registerEvents(Listeners(),plugin)
    }

    class Listeners: Listener {

        @EventHandler
        fun PlayerInteractEvent.onInteract() {
            if (hand == EquipmentSlot.OFF_HAND) return
            val block = FakeRegistry.getBlock(clickedBlock?.location ?: return) ?: return
            val e = PacketBlockInteractEvent(block,action)
            block.onInteract.accept(this)
            isCancelled = true

            Bukkit.getServer().pluginManager.callEvent(e)
        }

        @EventHandler
        fun PacketEntityInteractEvent.onEntityInteract() {
            val entity: PacketEntity = packetEntity
            entity.onInteract.accept(this)
        }
    }

}