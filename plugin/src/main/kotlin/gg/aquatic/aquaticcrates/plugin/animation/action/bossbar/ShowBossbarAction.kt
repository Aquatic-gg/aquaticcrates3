package gg.aquatic.aquaticcrates.plugin.animation.action.bossbar

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.plugin.animation.prop.BossbarAnimationProp
import gg.aquatic.aquaticseries.lib.action.AbstractAction
import gg.aquatic.aquaticseries.lib.adapt.AquaticBossBar
import gg.aquatic.aquaticseries.lib.util.argument.AquaticObjectArgument
import gg.aquatic.aquaticseries.lib.util.argument.impl.PrimitiveObjectArgument
import java.util.function.BiFunction

class ShowBossbarAction : AbstractAction<Animation>() {
    override fun run(binder: Animation, args: Map<String, Any?>, textUpdater: BiFunction<Animation, String, String>) {
        val id = args["id"] as String
        val message = args["message"] as String
        val color = args["color"] as String
        val style = args["style"] as String
        val progress = args["progress"] as Double

        val prop = BossbarAnimationProp(
            binder,
            message,
            AquaticBossBar.Color.valueOf(color.uppercase()),
            AquaticBossBar.Style.valueOf(style.uppercase()),
            progress
        )
        val previous = binder.props.put("bossbar:$id", prop)
        previous?.onAnimationEnd()
    }

    override fun arguments(): List<AquaticObjectArgument<*>> {
        return listOf(
            PrimitiveObjectArgument("id", "bossbar", true),
            PrimitiveObjectArgument("message", "", true),
            PrimitiveObjectArgument("color", "white", false),
            PrimitiveObjectArgument("style", "solid", false),
            PrimitiveObjectArgument("progress", 1.0, false),
        )
    }
}