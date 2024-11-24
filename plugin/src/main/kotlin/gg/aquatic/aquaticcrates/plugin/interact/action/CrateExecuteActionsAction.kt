package gg.aquatic.aquaticcrates.plugin.interact.action

import gg.aquatic.aquaticcrates.api.interaction.CrateInteractAction
import gg.aquatic.aquaticseries.lib.action.AbstractAction
import gg.aquatic.aquaticseries.lib.action.ConfiguredAction
import gg.aquatic.aquaticseries.lib.util.argument.AbstractObjectArgumentSerializer
import gg.aquatic.aquaticseries.lib.util.argument.AquaticObjectArgument
import gg.aquatic.aquaticseries.lib.util.executeActions
import gg.aquatic.aquaticseries.lib.util.getSectionList
import gg.aquatic.waves.registry.serializer.ActionSerializer
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import java.util.function.BiFunction

class CrateExecuteActionsAction : AbstractAction<CrateInteractAction>() {
    @Suppress("UNCHECKED_CAST")
    override fun run(
        binder: CrateInteractAction,
        args: Map<String, Any?>,
        textUpdater: BiFunction<CrateInteractAction, String, String>
    ) {
        val actions = args["actions"] as List<ConfiguredAction<Player>>
        actions.executeActions(binder.player) { _, str -> textUpdater.apply(binder, str) }
    }

    override fun arguments(): List<AquaticObjectArgument<*>> {
        return listOf(ActionsArgument("actions", true))
    }

    class ActionsArgument(id: String, required: Boolean) : AquaticObjectArgument<List<ConfiguredAction<Player>>>(
        id,
        arrayListOf(), required
    ) {
        override val serializer: AbstractObjectArgumentSerializer<List<ConfiguredAction<Player>>?>
            get() = Companion

        override fun load(section: ConfigurationSection): List<ConfiguredAction<Player>>? {
            return serializer.load(section, id) ?: defaultValue
        }

        companion object : AbstractObjectArgumentSerializer<List<ConfiguredAction<Player>>?>() {
            override fun load(section: ConfigurationSection, id: String): List<ConfiguredAction<Player>> {
                return ActionSerializer.fromSections(section.getSectionList(id))
            }
        }
    }
}