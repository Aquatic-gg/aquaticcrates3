package gg.aquatic.aquaticcrates.plugin.animation.action.path

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument

class BindPathAction : AbstractAction<Animation>(){
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("path-id", "path", true),
        PrimitiveObjectArgument("object-id", "model", true),
    )

    override fun execute(binder: Animation, args: Map<String, Any?>, textUpdater: (Animation, String) -> String) {
        TODO("Not yet implemented")
    }
}