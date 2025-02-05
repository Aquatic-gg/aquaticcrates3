package gg.aquatic.aquaticcrates.plugin.animation.action.timer

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationActions
import gg.aquatic.aquaticcrates.plugin.animation.prop.timer.TimedActionsAnimationProp
import gg.aquatic.waves.registry.serializer.ActionSerializer
import gg.aquatic.waves.util.argument.AbstractObjectArgumentSerializer
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.generic.Action
import gg.aquatic.waves.util.getSectionList
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import java.util.UUID

class TimedActionsAction: Action<Animation> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        TimedActionsArgument("actions", hashMapOf(), true)
    )

    override fun execute(binder: Animation, args: ObjectArguments, textUpdater: (Animation, String) -> String) {
        val actions = args.any("actions") as? HashMap<Int,CrateAnimationActions> ?: return

        val prop = TimedActionsAnimationProp(binder, actions)
        binder.props["timed-actions:${UUID.randomUUID()}"] = prop
        prop.tick()
    }

    class TimedActionsArgument(id: String,
                               defaultValue: HashMap<Int,CrateAnimationActions>?, required: Boolean
    ) : AquaticObjectArgument<HashMap<Int,CrateAnimationActions>>(id, defaultValue, required) {
        override val serializer: AbstractObjectArgumentSerializer<HashMap<Int,CrateAnimationActions>?>
            get() = Serializer

        override fun load(section: ConfigurationSection): HashMap<Int,CrateAnimationActions>? {
            return Serializer.load(section, id)
        }

        object Serializer : AbstractObjectArgumentSerializer<HashMap<Int,CrateAnimationActions>?>() {
            override fun load(
                section: ConfigurationSection,
                id: String
            ): HashMap<Int,CrateAnimationActions> {
                val map = hashMapOf<Int, CrateAnimationActions>()
                val actionsSection = section.getConfigurationSection(id) ?: return map
                for (key in actionsSection.getKeys(false)) {
                    val sections = actionsSection.getSectionList(key)
                    val actions = ActionSerializer.fromSections<Animation>(sections)
                    val playerBoundActions = ActionSerializer.fromSections<PlayerBoundAnimation>(sections)
                    map[key.toInt()] = CrateAnimationActions(
                        actions.toMutableList(),
                        playerBoundActions.toMutableList(),
                    )
                }
                return map
            }

        }
    }
}