package gg.aquatic.aquaticcrates.api.openprice

import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ArgumentSerializer
import gg.aquatic.waves.util.argument.ObjectArguments
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

interface Price {

    val arguments: List<AquaticObjectArgument<*>>
    fun take(player: Player, arguments: ObjectArguments, amount: Int)
    fun has(player: Player, arguments: ObjectArguments, amount: Int): Boolean

}

val PRICES = hashMapOf<String, Price>()

object PriceSerializer {

    fun fromSection(section: ConfigurationSection, crateId: String): ConfiguredPrice? {
        val type = section.getString("type") ?: return null
        val typeInstance = PRICES[type] ?: return null
        val args = ArgumentSerializer.load(section, typeInstance.arguments).toMutableMap()
        if (type.lowercase() == "crate-key" && !args.containsKey("crate")) {
            args += "crate" to crateId
        }
        return ConfiguredPrice(ObjectArguments(args), typeInstance)
    }

    fun fromSections(sections: List<ConfigurationSection>, crateId: String): List<ConfiguredPrice> = sections.mapNotNull { fromSection(it, crateId) }

}