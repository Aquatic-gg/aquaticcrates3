package gg.aquatic.aquaticcrates.plugin.animation.action.timer

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationActions
import gg.aquatic.aquaticcrates.api.util.ActionsArgument
import gg.aquatic.aquaticcrates.plugin.animation.prop.timer.TickerAnimationProp
import gg.aquatic.waves.util.action.RegisterAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.generic.Action

@RegisterAction("start-ticker")
class StartTickerAction : Action<Animation> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        ActionsArgument("actions", null, true),
        PrimitiveObjectArgument("tick-every", 1, false),
        PrimitiveObjectArgument("id", "example", false),
        PrimitiveObjectArgument("repeat-limit", -1, false)
    )

    override fun execute(binder: Animation, args: ObjectArguments, textUpdater: (Animation, String) -> String) {
        val actions = args.typed<CrateAnimationActions>("actions") ?: return
        val tickEvery = args.int("tick-every") { textUpdater(binder, it) } ?: return
        val repeatLimit = args.int("repeat-limit") { textUpdater(binder, it)} ?: -1
        val id = args.string("id") { textUpdater(binder, it) } ?: return

        val prop = TickerAnimationProp(binder, id, tickEvery, actions, repeatLimit)
        binder.props["ticker:$id"] = prop
    }
}