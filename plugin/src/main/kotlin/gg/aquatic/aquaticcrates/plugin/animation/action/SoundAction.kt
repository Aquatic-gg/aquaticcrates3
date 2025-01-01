package gg.aquatic.aquaticcrates.plugin.animation.action

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument

class SoundAction : AbstractAction<Animation>() {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("sound","",true),
        PrimitiveObjectArgument("pitch",1f,true),
        PrimitiveObjectArgument("volume",1f,true)
    )

    override fun execute(binder: Animation, args: Map<String, Any?>, textUpdater: (Animation, String) -> String) {
        val sound = args["sound"] as String
        val pitch = args["pitch"] as Float
        val volume = args["volume"] as Float
        binder.player.playSound(binder.player.location, sound, volume, pitch)
    }
}