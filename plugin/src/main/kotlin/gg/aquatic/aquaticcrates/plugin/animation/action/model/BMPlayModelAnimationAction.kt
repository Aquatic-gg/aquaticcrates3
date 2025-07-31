package gg.aquatic.aquaticcrates.plugin.animation.action.model

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.plugin.animation.idle.IdleAnimationImpl
import gg.aquatic.aquaticcrates.plugin.animation.prop.model.BMModelAnimationProp
import gg.aquatic.waves.interactable.type.BMInteractable
import gg.aquatic.waves.util.action.RegisterAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.generic.Action
import kr.toxicity.model.api.animation.AnimationModifier

@RegisterAction("play-bm-model-animation")
class BMPlayModelAnimationAction : Action<Animation> {

    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("id", null, false),
        PrimitiveObjectArgument("animation", "animation", true),
        PrimitiveObjectArgument("fade-in", 0, false),
        PrimitiveObjectArgument("fade-out", 0, false),
        PrimitiveObjectArgument("speed", 1.0f, false),
    )

    override fun execute(binder: Animation, args: ObjectArguments, textUpdater: (Animation, String) -> String) {
        val id = args.string("id") { textUpdater(binder, it) }
        val animation = args.string("animation") { textUpdater(binder, it) } ?: return
        val fadeIn = args.int("fade-in") { textUpdater(binder, it) } ?: return
        val fadeOut = args.int("fade-out") { textUpdater(binder, it) } ?: return
        val speed = args.float("speed") { textUpdater(binder, it) } ?: return

        if (id == null) {
            if (binder !is IdleAnimationImpl) return
            for (spawnedInteractable in binder.crate.spawnedInteractables) {
                if (spawnedInteractable is BMInteractable) {
                    spawnedInteractable.tracker.animate(
                        animation,
                        AnimationModifier(
                            fadeIn,
                            fadeOut,
                            speed
                        )
                    )
                }
            }
            return
        }
        val prop = binder.props["model:$id"] as? BMModelAnimationProp ?: return
        prop.playAnimation(animation, fadeIn, fadeOut, speed)
    }
}