package gg.aquatic.aquaticcrates.plugin.animation.action.path

import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathPoint
import gg.aquatic.aquaticseries.lib.util.argument.AbstractObjectArgumentSerializer
import gg.aquatic.aquaticseries.lib.util.argument.AquaticObjectArgument
import org.bukkit.configuration.ConfigurationSection
import java.util.TreeMap

class PathPointsArgument(
    id: String,
    defaultValue: TreeMap<Int, PathPoint>?, required: Boolean,
) : AquaticObjectArgument<TreeMap<Int, PathPoint>>(id, defaultValue, required) {

    override val serializer: AbstractObjectArgumentSerializer<TreeMap<Int, PathPoint>?> = Serializer

    override suspend fun load(section: ConfigurationSection): TreeMap<Int, PathPoint>? {
        return serializer.load(section, id)
    }

    object Serializer : AbstractObjectArgumentSerializer<TreeMap<Int, PathPoint>?>() {
        override suspend fun load(section: ConfigurationSection, id: String): TreeMap<Int, PathPoint>? {
            val map = TreeMap<Int, PathPoint>()
            for (key in section.getKeys(false)) {
                val s = section.getConfigurationSection(key) ?: continue
                val delay = key.toIntOrNull() ?: continue
                val x = s.getDouble("x")
                val y = s.getDouble("y")
                val z = s.getDouble("z")
                val yaw = s.getDouble("yaw").toFloat()
                val pitch = s.getDouble("pitch").toFloat()
                map += delay to PathPoint(x, y, z, yaw, pitch)
            }
            return map
        }
    }
}