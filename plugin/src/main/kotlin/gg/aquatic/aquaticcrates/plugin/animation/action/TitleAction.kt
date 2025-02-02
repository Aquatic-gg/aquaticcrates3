package gg.aquatic.aquaticcrates.plugin.animation.action

import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.waves.shadow.com.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetTitleSubtitle
import gg.aquatic.waves.shadow.com.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetTitleText
import gg.aquatic.waves.shadow.com.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetTitleTimes
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.toMMComponent
import gg.aquatic.waves.util.toUser

class TitleAction : AbstractAction<PlayerBoundAnimation>() {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("title", "", true),
        PrimitiveObjectArgument("subtitle", "", false),
        PrimitiveObjectArgument("fade-in", 0, false),
        PrimitiveObjectArgument("stay", 60, false),
        PrimitiveObjectArgument("fade-out", 0, false)
    )

    override fun execute(binder: PlayerBoundAnimation, args: ObjectArguments, textUpdater: (PlayerBoundAnimation, String) -> String) {
        val title = args.string("title") { textUpdater(binder, it) } ?: ""
        val subtitle = args.string("subtitle") { textUpdater(binder, it) } ?: ""
        val fadeIn = args.int("fade-in") { textUpdater(binder, it) } ?: 0
        val stay = args.int("stay") { textUpdater(binder, it) } ?: 60
        val fadeOut = args.int("fade-out") { textUpdater(binder, it) } ?: 0

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