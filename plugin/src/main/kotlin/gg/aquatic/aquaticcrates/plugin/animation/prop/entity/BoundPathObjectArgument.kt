package gg.aquatic.aquaticcrates.plugin.animation.prop.entity

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathBoundProperties
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathPoint
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathProp
import gg.aquatic.aquaticseries.lib.util.argument.AbstractObjectArgumentSerializer
import gg.aquatic.aquaticseries.lib.util.argument.AquaticObjectArgument
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection

class BoundPathObjectArgument(id: String,
                              defaultValue: ((Animation) -> MutableMap<PathProp, PathBoundProperties>)?, required: Boolean
) : AquaticObjectArgument<(Animation) -> MutableMap<PathProp, PathBoundProperties>>(id, defaultValue, required) {
    override val serializer: AbstractObjectArgumentSerializer<((Animation) -> MutableMap<PathProp, PathBoundProperties>)?> = Serializer

    override suspend fun load(section: ConfigurationSection): ((Animation) -> MutableMap<PathProp, PathBoundProperties>)? {
        return serializer.load(section, id)
    }

    object Serializer: AbstractObjectArgumentSerializer<((Animation) -> MutableMap<PathProp, PathBoundProperties>)?>() {
        override suspend fun load(section: ConfigurationSection, id: String): ((Animation) -> MutableMap<PathProp, PathBoundProperties>) {

            val section2 = section.getConfigurationSection(id) ?: return { _ -> hashMapOf() }
            return { animation: Animation ->
                val map = HashMap<PathProp, PathBoundProperties>()
                for (key in section2.getKeys(false)) {
                    val s = section2.getConfigurationSection(key)!!
                    Bukkit.getConsoleSender().sendMessage("Path path: ${s.currentPath}")
                    val pathProp = animation.props["path:$key"] as? PathProp?
                    if (pathProp == null) {
                        Bukkit.getConsoleSender().sendMessage("Could not find path with id of $key")
                        continue
                    }

                    val properties = PathBoundProperties(
                        PathPoint(
                            s.getDouble("offset.x"),
                            s.getDouble("offset.y"),
                            s.getDouble("offset.z"),
                            s.getDouble("offset.yaw").toFloat(),
                            s.getDouble("offset.pitch").toFloat()
                        ),
                        PathBoundProperties.OffsetType.valueOf(s.getString("offset.type","dynamic")!!.uppercase()),
                        s.getBoolean("affect-yaw-pitch", true)
                    )
                    map += pathProp to properties
                }

                Bukkit.getConsoleSender().sendMessage("Size: ${map.size}")
                map
            }

        }
    }
}