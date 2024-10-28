package gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.impl.display

import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.EntityAnimationProp
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityProperty
import gg.aquatic.aquaticcrates.plugin.animation.prop.entity.property.EntityPropertySerializer
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Display
import org.bukkit.entity.Entity
import org.bukkit.entity.TextDisplay
import org.bukkit.util.Transformation
import org.joml.AxisAngle4f
import org.joml.Quaternionf
import org.joml.Vector3f

class DisplayTransformProperty(
    val transformation: Transformation
) : EntityProperty {
    override fun apply(entity: Entity, prop: EntityAnimationProp) {
        val display = entity as? Display ?: return
        display.transformation = transformation
    }

    object Serializer : EntityPropertySerializer {
        override suspend fun load(section: ConfigurationSection): EntityProperty {
            val s = section.getConfigurationSection("transformation") ?: return DisplayTransformProperty(
                Transformation(
                    Vector3f(),
                    AxisAngle4f(),
                    Vector3f(),
                    AxisAngle4f()
                )
            )

            val scaleStr = s.getString("scale")
            val scale = if (scaleStr != null) {
                val split = scaleStr.split(";")
                Vector3f(split[0].toFloat(), split[1].toFloat(), split[2].toFloat())
            } else Vector3f(1f, 1f, 1f)

            val translationStr = s.getString("translation")
            val translation = if (translationStr != null) {
                val split = translationStr.split(";")
                Vector3f(split[0].toFloat(), split[1].toFloat(), split[2].toFloat())
            }
            else Vector3f(0f, 0f, 0f)

            val rotationStr = s.getString("rotation")
            val rotation = if (rotationStr != null) {
                val split = rotationStr.split(";")
                if (split.size > 3) {
                    Quaternionf(split[0].toFloat(), split[1].toFloat(), split[2].toFloat(), split[3].toFloat())
                }
                else Quaternionf().rotationXYZ(split[0].toFloat(), split[1].toFloat(), split[2].toFloat())
            } else Quaternionf()

            return DisplayTransformProperty(Transformation(translation, rotation, scale, Quaternionf()))
        }

    }
}