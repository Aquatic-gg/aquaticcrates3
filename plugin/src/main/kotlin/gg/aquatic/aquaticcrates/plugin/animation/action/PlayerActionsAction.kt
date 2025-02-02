package gg.aquatic.aquaticcrates.plugin.animation.action

import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.waves.registry.serializer.ActionSerializer
import gg.aquatic.waves.util.argument.AbstractObjectArgumentSerializer
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.generic.Action
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import gg.aquatic.waves.util.getSectionList
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

class PlayerActionsAction : Action<PlayerBoundAnimation> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PlayerActionsArgument("actions", listOf(), true)
    )

    override fun execute(
        binder: PlayerBoundAnimation,
        args: ObjectArguments,
        textUpdater: (PlayerBoundAnimation, String) -> String
    ) {
        val actions = args.typed<List<ConfiguredExecutableObject<Player, Unit>>>("actions") ?: listOf()
        for (action in actions) {
            action.execute(binder.player) { _, str ->
                textUpdater(binder, str)
            }
        }
    }

    class PlayerActionsArgument(
        id: String,
        defaultValue: List<ConfiguredExecutableObject<Player, Unit>>?, required: Boolean
    ) : AquaticObjectArgument<List<ConfiguredExecutableObject<Player, Unit>>>(id, defaultValue, required) {
        override val serializer: AbstractObjectArgumentSerializer<List<ConfiguredExecutableObject<Player, Unit>>?> =
            Companion

        override fun load(section: ConfigurationSection): List<ConfiguredExecutableObject<Player, Unit>>? {
            return serializer.load(section, id)
        }

        companion object : AbstractObjectArgumentSerializer<List<ConfiguredExecutableObject<Player, Unit>>?>() {
            override fun load(
                section: ConfigurationSection,
                id: String
            ): List<ConfiguredExecutableObject<Player, Unit>> {
                return ActionSerializer.fromSections<Player>(section.getSectionList("actions"))
            }

        }
    }
}