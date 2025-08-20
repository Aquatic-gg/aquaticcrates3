package gg.aquatic.aquaticcrates.plugin.animation.action.showcase

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.plugin.animation.prop.showcase.RewardShowcaseAnimationProp
import gg.aquatic.waves.util.action.RegisterAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.collection.executeActions
import gg.aquatic.waves.util.generic.Action

@RegisterAction("hide-reward-showcase")
class HideRewardShowcaseAction: Action<Animation> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("id", "example", true),
    )

    override fun execute(
        binder: Animation,
        args: ObjectArguments,
        textUpdater: (Animation, String) -> String
    ) {
        val id = args.string("id") { textUpdater(binder, it) } ?: return
        val prop = binder.props["reward-showcase:$id"] as? RewardShowcaseAnimationProp ?: return
        prop.onAnimationEnd()
        prop.showcaseHandle?.showcase?.despawnActions?.executeActions(binder as PlayerBoundAnimation) { p, str -> textUpdater(p, str)}
        binder.props.remove("reward-showcase:$id")
    }
}