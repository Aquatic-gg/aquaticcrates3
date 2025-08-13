package gg.aquatic.aquaticcrates.plugin.awaiters

import gg.aquatic.waves.api.event.event
import net.momirealms.craftengine.bukkit.api.event.CraftEngineReloadEvent
import java.util.concurrent.CompletableFuture

class CEAwaiter: AbstractAwaiter() {
    override val future: CompletableFuture<Void> = CompletableFuture()

    init {
        event<CraftEngineReloadEvent> {
            future.complete(null)
            loaded = true
        }
    }
}