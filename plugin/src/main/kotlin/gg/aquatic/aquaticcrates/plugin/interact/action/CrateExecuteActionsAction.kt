package gg.aquatic.aquaticcrates.plugin.interact.action

import gg.aquatic.aquaticcrates.api.interaction.CrateInteractAction
import gg.aquatic.waves.registry.serializer.ActionSerializer
import gg.aquatic.waves.util.action.RegisterAction
import gg.aquatic.waves.util.argument.AbstractObjectArgumentSerializer
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.collection.executeActions
import gg.aquatic.waves.util.generic.Action
import gg.aquatic.waves.util.generic.ConfiguredExecutableObject
import gg.aquatic.waves.util.getSectionList
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

@RegisterAction("execute-actions")
class CrateExecuteActionsAction : Action<CrateInteractAction> {

    override val arguments: List<AquaticObjectArgument<*>> = listOf(ActionsArgument("actions", true))

    override fun execute(
        binder: CrateInteractAction,
        args: ObjectArguments,
        textUpdater: (CrateInteractAction, String) -> String
    ) {
        val actions = args.typed<List<ConfiguredExecutableObject<Player,Unit>>>("actions") ?: return
        actions.executeActions(binder.player) { _, str -> textUpdater(binder, str)
        }
    }

    class ActionsArgument(id: String, required: Boolean) : AquaticObjectArgument<List<ConfiguredExecutableObject<Player,Unit>>>(
        id,
        arrayListOf(), required
    ) {
        override val serializer: AbstractObjectArgumentSerializer<List<ConfiguredExecutableObject<Player,Unit>>?>
            get() = Companion

        override fun load(section: ConfigurationSection): List<ConfiguredExecutableObject<Player,Unit>>? {
            return serializer.load(section, id) ?: defaultValue
        }

        companion object : AbstractObjectArgumentSerializer<List<ConfiguredExecutableObject<Player,Unit>>?>() {
            override fun load(section: ConfigurationSection, id: String): List<ConfiguredExecutableObject<Player,Unit>> {
                return ActionSerializer.fromSections(section.getSectionList(id))
            }
        }
    }
}