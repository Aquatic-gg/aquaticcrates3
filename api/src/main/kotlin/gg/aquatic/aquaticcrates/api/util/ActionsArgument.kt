package gg.aquatic.aquaticcrates.api.util

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.waves.registry.serializer.ActionSerializer
import gg.aquatic.waves.util.argument.AbstractObjectArgumentSerializer
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import gg.aquatic.waves.util.getSectionList
import org.bukkit.configuration.ConfigurationSection

class ActionsArgument(id: String,
                      defaultValue: List<ConfiguredExecutableObject<Animation, Unit>>?, required: Boolean
) : AquaticObjectArgument<List<ConfiguredExecutableObject<Animation,Unit>>>(id, defaultValue, required) {
    override val serializer: AbstractObjectArgumentSerializer<List<ConfiguredExecutableObject<Animation, Unit>>?>
        get() = Serializer

    override fun load(section: ConfigurationSection): List<ConfiguredExecutableObject<Animation, Unit>>? {
        return Serializer.load(section, id)
    }

    object Serializer: AbstractObjectArgumentSerializer<List<ConfiguredExecutableObject<Animation, Unit>>?>() {
        override fun load(
            section: ConfigurationSection,
            id: String
        ): List<ConfiguredExecutableObject<Animation, Unit>> {
            val actions = ActionSerializer.fromSections<Animation>(section.getSectionList(id))
            return actions
        }

    }
}