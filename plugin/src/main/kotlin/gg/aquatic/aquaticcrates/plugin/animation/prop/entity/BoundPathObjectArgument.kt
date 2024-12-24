package gg.aquatic.aquaticcrates.plugin.animation.prop.entity

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathBoundProperties
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathPoint
import gg.aquatic.aquaticcrates.plugin.animation.prop.path.PathProp

import gg.aquatic.waves.util.argument.AbstractObjectArgumentSerializer
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import java.util.concurrent.ConcurrentHashMap

class BoundPathObjectArgument(id: String,
                              defaultValue: ((Animation) -> ConcurrentHashMap<PathProp, PathBoundProperties>)?, required: Boolean
) : AquaticObjectArgument<(Animation) -> ConcurrentHashMap<PathProp, PathBoundProperties>>(id, defaultValue, required) {
    override val serializer: AbstractObjectArgumentSerializer<((Animation) -> ConcurrentHashMap<PathProp, PathBoundProperties>)?> = Serializer

    override fun load(section: ConfigurationSection): ((Animation) -> ConcurrentHashMap<PathProp, PathBoundProperties>)? {
        return serializer.load(section, id)
    }

    object Serializer: AbstractObjectArgumentSerializer<((Animation) -> ConcurrentHashMap<PathProp, PathBoundProperties>)?>() {
        override fun load(section: ConfigurationSection, id: String): ((Animation) -> ConcurrentHashMap<PathProp, PathBoundProperties>) {

            val section2 = section.getConfigurationSection(id) ?: return { _ -> ConcurrentHashMap() }
            return { animation: Animation ->
                val map = ConcurrentHashMap<PathProp, PathBoundProperties>()
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