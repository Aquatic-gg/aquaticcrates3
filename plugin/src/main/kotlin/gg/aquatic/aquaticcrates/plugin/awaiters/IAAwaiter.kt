package gg.aquatic.aquaticcrates.plugin.awaiters

import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent
import gg.aquatic.waves.api.event.event
import java.util.concurrent.CompletableFuture

class IAAwaiter: AbstractAwaiter() {
    override val future: CompletableFuture<Void> = CompletableFuture()

    init {
        event<ItemsAdderLoadDataEvent> {
            future.complete(null)
            loaded = true
        }
    }
}