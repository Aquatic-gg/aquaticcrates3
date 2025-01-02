package gg.aquatic.aquaticcrates.plugin.animation.action.bossbar

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.plugin.animation.prop.BossbarAnimationProp
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import net.kyori.adventure.bossbar.BossBar

class ShowBossbarAction : AbstractAction<Animation>() {

    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("id", "bossbar", true),
        PrimitiveObjectArgument("message", "", true),
        PrimitiveObjectArgument("color", "white", false),
        PrimitiveObjectArgument("style", "solid", false),
        PrimitiveObjectArgument("progress", 1.0, false),
    )

    override fun execute(binder: Animation, args: Map<String, Any?>, textUpdater: (Animation, String) -> String) {
        val id = args["id"] as String
        val message = args["message"] as String
        val color = args["color"] as String
        val style = args["style"] as String
        val progress = args["progress"].toString().toFloat()

        val prop = BossbarAnimationProp(
            binder,
            textUpdater(binder, message),
            BossBar.Color.valueOf(color.uppercase()),
            BossBar.Overlay.valueOf(style.uppercase()),
            progress
        )
        val previous = binder.props.put("bossbar:$id", prop)
        previous?.onAnimationEnd()
    }
}