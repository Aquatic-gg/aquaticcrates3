package gg.aquatic.aquaticcrates.plugin.awaiters

import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent
import dev.lone.itemsadder.api.ItemsAdder
import gg.aquatic.waves.api.event.event
import java.util.concurrent.CompletableFuture

class IAAwaiter: AbstractAwaiter() {
    override val future: CompletableFuture<Void> = CompletableFuture()

    init {
        if (ItemsAdder.areItemsLoaded()) {
            future.complete(null)
            loaded = true
        } else {
            event<ItemsAdderLoadDataEvent> {
                future.complete(null)
                loaded = true
            }
        }
    }
}