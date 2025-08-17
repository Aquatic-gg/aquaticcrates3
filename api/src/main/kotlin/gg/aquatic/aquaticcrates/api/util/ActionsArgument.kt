package gg.aquatic.aquaticcrates.api.util

import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.waves.registry.serializer.ActionSerializer
import gg.aquatic.waves.util.argument.AbstractObjectArgumentSerializer
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.generic.ClassTransform
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import gg.aquatic.waves.util.getSectionList
import org.bukkit.configuration.ConfigurationSection

class ActionsArgument(
    id: String,
    defaultValue: Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>?, required: Boolean
) : AquaticObjectArgument<Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>>(
    id,
    defaultValue,
    required
) {
    override val serializer: AbstractObjectArgumentSerializer<Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>?>
        get() = Serializer

    override fun load(section: ConfigurationSection): Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>? {
        return Serializer.load(section, id)
    }

    object Serializer :
        AbstractObjectArgumentSerializer<Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>?>() {
        override fun load(
            section: ConfigurationSection,
            id: String
        ): Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>> {
            val actions = ActionSerializer.fromSections<PlayerBoundAnimation>(
                section.getSectionList(id),
                ClassTransform(PlayerBoundAnimation::class.java, { a -> a.player })
            )

            return actions
        }

    }
}