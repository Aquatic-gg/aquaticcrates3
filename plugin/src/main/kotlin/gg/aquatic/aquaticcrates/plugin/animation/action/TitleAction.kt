package gg.aquatic.aquaticcrates.plugin.animation.action

import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.generic.Action
import gg.aquatic.waves.util.toMMComponent
import net.kyori.adventure.title.Title
import java.time.Duration

class TitleAction : Action<PlayerBoundAnimation> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("title", "", true),
        PrimitiveObjectArgument("subtitle", "", false),
        PrimitiveObjectArgument("fade-in", 0, false),
        PrimitiveObjectArgument("stay", 60, false),
        PrimitiveObjectArgument("fade-out", 0, false)
    )

    override fun execute(
        binder: PlayerBoundAnimation,
        args: ObjectArguments,
        textUpdater: (PlayerBoundAnimation, String) -> String
    ) {

        val title = args.string("title") { textUpdater(binder, it) } ?: ""
        val subtitle = args.string("subtitle") { textUpdater(binder, it) } ?: ""
        val fadeIn = args.int("fade-in") { textUpdater(binder, it) } ?: 0
        val stay = args.int("stay") { textUpdater(binder, it) } ?: 60
        val fadeOut = args.int("fade-out") { textUpdater(binder, it) } ?: 0

        binder.player.showTitle(
            Title.title(
                binder.updatePlaceholders(title).toMMComponent(),
                binder.updatePlaceholders(subtitle).toMMComponent(),
                Title.Times.times(
                    Duration.ofMillis(fadeIn.toLong() * 50),
                    Duration.ofMillis(stay.toLong() * 50),
                    Duration.ofMillis(fadeOut.toLong() * 50),
                )
            )
        )
    }
}