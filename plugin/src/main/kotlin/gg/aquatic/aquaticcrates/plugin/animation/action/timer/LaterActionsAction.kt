package gg.aquatic.aquaticcrates.plugin.animation.action.timer

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.api.util.ActionsArgument
import gg.aquatic.aquaticcrates.plugin.animation.prop.timer.LaterActionsAnimationProp
import gg.aquatic.waves.util.action.RegisterAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.generic.Action
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import java.util.UUID

@RegisterAction("delayed-actions")
class LaterActionsAction: Action<PlayerBoundAnimation> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("delay", 0, true),
        ActionsArgument("actions", null, true),
    )

    override fun execute(binder: PlayerBoundAnimation, args: ObjectArguments, textUpdater: (PlayerBoundAnimation, String) -> String) {
        val delay = args.int("delay") { textUpdater(binder, it) } ?: return
        val actions = args.typed<Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>>("actions") ?: return
        val prop = LaterActionsAnimationProp(
            binder,
            actions,
            delay
        )
        binder.props["later-actions:${UUID.randomUUID()}"] = prop
    }
}