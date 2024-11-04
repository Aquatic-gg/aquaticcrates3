package gg.aquatic.aquaticcrates.api.util.animationitem

import gg.aquatic.aquaticseries.lib.util.argument.AbstractObjectArgumentSerializer
import gg.aquatic.aquaticseries.lib.util.argument.AquaticObjectArgument
import gg.aquatic.waves.item.AquaticItem
import gg.aquatic.waves.util.loadFromYml
import org.bukkit.configuration.ConfigurationSection

class ACratesItemArgument(id: String, defaultValue: ArgumentItem?, required: Boolean) :
    AquaticObjectArgument<ArgumentItem>(
        id, defaultValue,
        required
    ) {
    override val serializer: AbstractObjectArgumentSerializer<ArgumentItem?> = Serializer

    override suspend fun load(section: ConfigurationSection): ArgumentItem {
        return Serializer.load(section, id)
    }

    object Serializer : AbstractObjectArgumentSerializer<ArgumentItem?>() {
        override suspend fun load(section: ConfigurationSection, id: String): ArgumentItem {
            val s = section.getConfigurationSection(id) ?: return ArgumentItem("rewarditem:0", null)
            val type = s.getString("type") ?: "regular"
            val baseItem = AquaticItem.loadFromYml(s)
            return ArgumentItem(type, baseItem)
        }
    }
}