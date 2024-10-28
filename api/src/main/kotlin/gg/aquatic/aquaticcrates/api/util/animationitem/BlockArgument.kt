package gg.aquatic.aquaticcrates.api.util.animationitem

import gg.aquatic.aquaticseries.lib.block.AquaticBlock
import gg.aquatic.aquaticseries.lib.util.AquaticBlockSerializer
import gg.aquatic.aquaticseries.lib.util.argument.AbstractObjectArgumentSerializer
import gg.aquatic.aquaticseries.lib.util.argument.AquaticObjectArgument
import org.bukkit.configuration.ConfigurationSection

class BlockArgument(id: String, defaultValue: AquaticBlock?, required: Boolean) : AquaticObjectArgument<AquaticBlock>(id, defaultValue,
    required
) {
    override val serializer: AbstractObjectArgumentSerializer<AquaticBlock?> = Serializer

    override suspend fun load(section: ConfigurationSection): AquaticBlock? {
        return serializer.load(section, id)
    }

    object Serializer : AbstractObjectArgumentSerializer<AquaticBlock?>() {
        override suspend fun load(section: ConfigurationSection, id: String): AquaticBlock? {
            val s = section.getConfigurationSection(id) ?: return null
            return AquaticBlockSerializer.load(s)
        }

    }
}