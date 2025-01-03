package gg.aquatic.aquaticcrates.plugin.animation.action

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.waves.shadow.com.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetTitleSubtitle
import gg.aquatic.waves.shadow.com.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetTitleText
import gg.aquatic.waves.shadow.com.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetTitleTimes
import gg.aquatic.waves.shadow.com.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTitle
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.toMMComponent
import gg.aquatic.waves.util.toUser
import org.bukkit.Bukkit

class TitleAction : AbstractAction<Animation>() {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("title", "", true),
        PrimitiveObjectArgument("subtitle", "", false),
        PrimitiveObjectArgument("fade-in", 0, false),
        PrimitiveObjectArgument("stay", 60, false),
        PrimitiveObjectArgument("fade-out", 0, false)
    )

    override fun execute(binder: Animation, args: Map<String, Any?>, textUpdater: (Animation, String) -> String) {
        val title = (args["title"] as String)
        val subtitle = (args["subtitle"] as String)
        val fadeIn = args["fade-in"] as Int
        val stay = args["stay"] as Int
        val fadeOut = args["fade-out"] as Int

        val packets = listOf(
            WrapperPlayServerSetTitleText(binder.updatePlaceholders(title).toMMComponent()),
            WrapperPlayServerSetTitleTimes(fadeIn, stay, fadeOut),
            WrapperPlayServerSetTitleSubtitle(binder.updatePlaceholders(subtitle).toMMComponent())
        )

        binder.player.toUser().let {
            packets.forEach { packet -> it.sendPacket(packet) }
        }
    }
}