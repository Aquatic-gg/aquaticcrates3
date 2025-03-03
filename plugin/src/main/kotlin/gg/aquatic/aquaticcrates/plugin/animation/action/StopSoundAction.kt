package gg.aquatic.aquaticcrates.plugin.animation.action

import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.generic.Action

class StopSoundAction : Action<PlayerBoundAnimation> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("sound", "example", true),
    )

    override fun execute(binder: PlayerBoundAnimation, args: ObjectArguments, textUpdater: (PlayerBoundAnimation, String) -> String) {
        val sound = args.string("sound") { str -> textUpdater(binder, str) } ?: return
        binder.player.stopSound(sound)
    }
}