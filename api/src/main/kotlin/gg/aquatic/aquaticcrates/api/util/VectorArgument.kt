package gg.aquatic.aquaticcrates.api.util

import gg.aquatic.aquaticseries.lib.util.argument.AbstractObjectArgumentSerializer
import gg.aquatic.aquaticseries.lib.util.argument.AquaticObjectArgument
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.util.Vector

class VectorArgument(id: String, defaultValue: Vector?, required: Boolean) : AquaticObjectArgument<Vector>(id, defaultValue,
    required
) {
    override val serializer: AbstractObjectArgumentSerializer<Vector?> = Serializer

    override suspend fun load(section: ConfigurationSection): Vector? {
        return Serializer.load(section, id) ?: defaultValue
    }

    object Serializer: AbstractObjectArgumentSerializer<Vector?>() {
        override suspend fun load(section: ConfigurationSection, id: String): Vector? {
            val str = section.getString(id) ?: return null
            val split = str.split(";")
            if (split.size != 3) {
                return null
            }
            val vector = Vector(split[0].toDouble(), split[1].toDouble(), split[2].toDouble())
            return vector
        }
    }
}