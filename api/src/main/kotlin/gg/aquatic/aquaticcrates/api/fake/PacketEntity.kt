package gg.aquatic.aquaticcrates.api.fake

import gg.aquatic.aquaticcrates.api.fake.event.PacketEntityInteractEvent
import org.bukkit.entity.Entity
import org.bukkit.util.Consumer

abstract class PacketEntity: FakeObject() {

    abstract val bukkitEntity: Entity
    abstract val onInteract: Consumer<PacketEntityInteractEvent>
    abstract val entityId: Int

}