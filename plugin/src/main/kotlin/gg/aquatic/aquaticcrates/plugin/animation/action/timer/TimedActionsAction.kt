package gg.aquatic.aquaticcrates.plugin.animation.action.timer

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.plugin.animation.prop.timer.TimedActionsAnimationProp
import gg.aquatic.waves.registry.serializer.ActionSerializer
import gg.aquatic.waves.util.action.RegisterAction
import gg.aquatic.waves.util.argument.AbstractObjectArgumentSerializer
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.generic.Action
import gg.aquatic.waves.util.generic.ClassTransform
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import gg.aquatic.waves.util.getSectionList
import org.bukkit.configuration.ConfigurationSection
import java.util.*

@RegisterAction("timed-actions")
class TimedActionsAction: Action<PlayerBoundAnimation> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        TimedActionsArgument("actions", hashMapOf(), true)
    )

    @Suppress("UNCHECKED_CAST")
    override fun execute(binder: PlayerBoundAnimation, args: ObjectArguments, textUpdater: (PlayerBoundAnimation, String) -> String) {
        val actions = args.any("actions") as? HashMap<Int,Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>> ?: return

        val prop = TimedActionsAnimationProp(binder, actions)
        binder.props["timed-actions:${UUID.randomUUID()}"] = prop
        prop.tick()
    }

    class TimedActionsArgument(id: String,
                               defaultValue: HashMap<Int,Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>>?, required: Boolean
    ) : AquaticObjectArgument<HashMap<Int,Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>>>(id, defaultValue, required) {
        override val serializer: AbstractObjectArgumentSerializer<HashMap<Int,Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>>?>
            get() = Serializer

        override fun load(section: ConfigurationSection): HashMap<Int,Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>>? {
            return Serializer.load(section, id)
        }

        object Serializer : AbstractObjectArgumentSerializer<HashMap<Int,Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>>?>() {
            override fun load(
                section: ConfigurationSection,
                id: String
            ): HashMap<Int,Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>> {
                val map = hashMapOf<Int, Collection<ConfiguredExecutableObject<PlayerBoundAnimation, Unit>>>()
                val actionsSection = section.getConfigurationSection(id) ?: return map
                for (key in actionsSection.getKeys(false)) {
                    val sections = actionsSection.getSectionList(key)
                    val actions = ActionSerializer.fromSections<PlayerBoundAnimation>(sections, ClassTransform(
                        PlayerBoundAnimation::class.java
                    ) { a -> a.player })
                    map[key.toInt()] = actions
                }
                return map
            }

        }
    }
}