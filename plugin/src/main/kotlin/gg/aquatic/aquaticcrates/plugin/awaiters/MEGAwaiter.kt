package gg.aquatic.aquaticcrates.plugin.awaiters

import com.ticxo.modelengine.api.ModelEngineAPI
import com.ticxo.modelengine.api.events.ModelRegistrationEvent
import com.ticxo.modelengine.api.generator.ModelGenerator
import gg.aquatic.waves.api.event.event
import java.util.concurrent.CompletableFuture

class MEGAwaiter: AbstractAwaiter() {
    override val future: CompletableFuture<Void> = CompletableFuture()

    init {
        if (ModelEngineAPI.getAPI().modelGenerator.isInitialized) {
            future.complete(null)
            loaded = true
        } else {
            event<ModelRegistrationEvent> {
                if (it.phase == ModelGenerator.Phase.FINISHED) {
                    future.complete(null)
                    loaded = true
                }
            }
        }
    }
}