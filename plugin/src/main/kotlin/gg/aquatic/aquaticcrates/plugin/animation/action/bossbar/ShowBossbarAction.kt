package gg.aquatic.aquaticcrates.plugin.animation.action.bossbar

import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.plugin.animation.prop.BossbarAnimationProp
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import net.kyori.adventure.bossbar.BossBar

class ShowBossbarAction : AbstractAction<PlayerBoundAnimation>() {

    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("id", "bossbar", true),
        PrimitiveObjectArgument("message", "", true),
        PrimitiveObjectArgument("color", "white", false),
        PrimitiveObjectArgument("style", "solid", false),
        PrimitiveObjectArgument("progress", 1.0, false),
    )

    override fun execute(
        binder: PlayerBoundAnimation,
        args: ObjectArguments,
        textUpdater: (PlayerBoundAnimation, String) -> String
    ) {
        val id = args.string("id") { textUpdater(binder, it) } ?: return
        val message = args.string("message") { textUpdater(binder, it)} ?: return
        val color = args.string("color") { textUpdater(binder, it) } ?: return
        val style = args.string("style") { textUpdater(binder, it) } ?: return
        val progress = args.float("progress") { textUpdater(binder, it) } ?: 1.0f

        val prop = BossbarAnimationProp(
            binder,
            message,
            BossBar.Color.valueOf(color.uppercase()),
            BossBar.Overlay.valueOf(style.uppercase()),
            progress
        )
        val previous = binder.props.put("bossbar:$id", prop)
        previous?.onAnimationEnd()
    }
}