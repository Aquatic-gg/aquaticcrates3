package gg.aquatic.aquaticcrates.plugin.animation.action

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument

class SoundAction : AbstractAction<Animation>() {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("sound","",true),
        PrimitiveObjectArgument("pitch",1f,false),
        PrimitiveObjectArgument("volume",100f,false)
    )

    override fun execute(binder: Animation, args: Map<String, Any?>, textUpdater: (Animation, String) -> String) {
        val sound = args["sound"] as String
        val pitch = args["pitch"].toString().toFloat()
        val volume = args["volume"].toString().toFloat()
        binder.player.playSound(binder.player.location, sound, volume, pitch)
    }
}