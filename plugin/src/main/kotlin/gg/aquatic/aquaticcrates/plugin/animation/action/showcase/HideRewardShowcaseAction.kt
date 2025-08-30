package gg.aquatic.aquaticcrates.plugin.animation.action.showcase

import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.plugin.animation.prop.showcase.RewardShowcaseAnimationProp
import gg.aquatic.waves.util.action.RegisterAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.collection.executeActions
import gg.aquatic.waves.util.generic.Action
import net.kyori.adventure.key.Key

@RegisterAction("hide-reward-showcase")
class HideRewardShowcaseAction: Action<CrateAnimation> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("id", "example", true),
    )

    override fun execute(
        binder: CrateAnimation,
        args: ObjectArguments,
        textUpdater: (CrateAnimation, String) -> String
    ) {
        val id = args.string("id") { textUpdater(binder, it) } ?: return
        val key = Key.key("reward-showcase:$id")
        val prop = binder.props[key] as? RewardShowcaseAnimationProp ?: return
        prop.onEnd()
        prop.showcaseHandle?.showcase?.despawnActions?.executeActions(binder) { p, str -> textUpdater(p, str)}
        binder.props.remove(key)
    }
}