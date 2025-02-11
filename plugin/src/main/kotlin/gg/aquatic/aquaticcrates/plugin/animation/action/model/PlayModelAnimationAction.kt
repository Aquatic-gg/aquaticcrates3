package gg.aquatic.aquaticcrates.plugin.animation.action.model

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.plugin.animation.idle.IdleAnimationImpl
import gg.aquatic.aquaticcrates.plugin.animation.prop.model.ModelAnimationProp
import gg.aquatic.waves.interactable.type.MEGInteractable
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.generic.Action

class PlayModelAnimationAction : Action<Animation> {

    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("id", null, false),
        PrimitiveObjectArgument("animation", "animation", true),
        PrimitiveObjectArgument("fade-in", 0.0, false),
        PrimitiveObjectArgument("fade-out", 0.0, false),
        PrimitiveObjectArgument("speed", 1.0, false),
    )

    override fun execute(binder: Animation, args: ObjectArguments, textUpdater: (Animation, String) -> String) {
        val id = args.string("id") { textUpdater(binder, it) }
        val animation = args.string("animation") { textUpdater(binder, it) } ?: return
        val fadeIn = args.double("fade-in") { textUpdater(binder, it) } ?: return
        val fadeOut = args.double("fade-out") { textUpdater(binder, it) } ?: return
        val speed = args.double("speed") { textUpdater(binder, it) } ?: return

        if (id == null) {
            if (binder !is IdleAnimationImpl) return
            for (spawnedInteractable in binder.crate.spawnedInteractables) {
                if (spawnedInteractable is MEGInteractable) {
                    spawnedInteractable.activeModel?.animationHandler?.playAnimation(
                        animation,
                        fadeIn,
                        fadeOut,
                        speed,
                        true
                    )
                }
            }
            return
        }
        val prop = binder.props["model:$id"] as? ModelAnimationProp ?: return
        prop.playAnimation(animation, fadeIn, fadeOut, speed)
    }
}