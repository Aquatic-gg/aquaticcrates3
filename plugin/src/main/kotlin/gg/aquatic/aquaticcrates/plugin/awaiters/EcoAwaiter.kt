package gg.aquatic.aquaticcrates.plugin.awaiters

import gg.aquatic.waves.util.runLaterSync
import java.util.concurrent.CompletableFuture

class EcoAwaiter: AbstractAwaiter() {
    override val future: CompletableFuture<Void> = CompletableFuture()

    init {
        runLaterSync(10*20) {
            future.complete(null)
            loaded = true
        }
    }
}