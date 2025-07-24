package gg.aquatic.aquaticcrates.plugin.awaiters

import com.nexomc.nexo.api.events.NexoItemsLoadedEvent
import gg.aquatic.waves.api.event.event
import java.util.concurrent.CompletableFuture

class NexoAwaiter: AbstractAwaiter() {
    override val future: CompletableFuture<Void> = CompletableFuture()

    init {
        event<NexoItemsLoadedEvent> {
            future.complete(null)
            loaded = true
        }
    }
}