package gg.aquatic.aquaticcrates.plugin.animation.action.bossbar

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.plugin.animation.prop.BossbarAnimationProp
import gg.aquatic.aquaticseries.lib.action.AbstractAction
import gg.aquatic.aquaticseries.lib.adapt.AquaticBossBar
import gg.aquatic.aquaticseries.lib.util.argument.AquaticObjectArgument
import gg.aquatic.aquaticseries.lib.util.argument.impl.PrimitiveObjectArgument
import java.util.function.BiFunction

class SetBossbarStyleAction : AbstractAction<Animation>() {
    override fun run(binder: Animation, args: Map<String, Any?>, textUpdater: BiFunction<Animation, String, String>) {
        val id = args["id"] as String
        val style = args["style"] as String
        val prop = binder.props["bossbar:$id"] as? BossbarAnimationProp? ?: return
        prop.bossBar.style = AquaticBossBar.Style.valueOf(style.uppercase())
    }

    override fun arguments(): List<AquaticObjectArgument<*>> {
        return listOf(
            PrimitiveObjectArgument("id", "bossbar", true),
            PrimitiveObjectArgument("style", "solid", true),
        )
    }
}