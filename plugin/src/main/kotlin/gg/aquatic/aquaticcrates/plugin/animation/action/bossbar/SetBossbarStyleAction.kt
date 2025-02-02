package gg.aquatic.aquaticcrates.plugin.animation.action.bossbar

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.plugin.animation.prop.BossbarAnimationProp
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import net.kyori.adventure.bossbar.BossBar

class SetBossbarStyleAction : AbstractAction<Animation>() {

    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("id", "bossbar", true),
        PrimitiveObjectArgument("style", "solid", true),
    )

    override fun execute(binder: Animation, args: ObjectArguments, textUpdater: (Animation, String) -> String) {
        val id = args.string("id") { textUpdater(binder, it) } ?: return
        val style = args.string("style") { textUpdater(binder, it) } ?: return
        val prop = binder.props["bossbar:$id"] as? BossbarAnimationProp? ?: return
        prop.bossBar.overlay = BossBar.Overlay.valueOf(style.uppercase())
    }
}