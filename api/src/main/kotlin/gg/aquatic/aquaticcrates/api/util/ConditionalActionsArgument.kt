package gg.aquatic.aquaticcrates.api.util

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.waves.registry.serializer.ActionSerializer
import gg.aquatic.waves.registry.serializer.RequirementSerializer
import gg.aquatic.waves.util.argument.AbstractObjectArgumentSerializer
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.generic.ConfiguredExecutableObjectWithConditions
import gg.aquatic.waves.util.generic.ConfiguredExecutableObjectsWithConditions
import gg.aquatic.waves.util.getSectionList
import gg.aquatic.waves.util.requirement.ConfiguredRequirementWithFailActions
import org.bukkit.configuration.ConfigurationSection
import java.util.ArrayList

class ConditionalActionsArgument(id: String, defaultValue: ConfiguredExecutableObjectsWithConditions<Animation, Unit>?,
                                 required: Boolean
) : AquaticObjectArgument<ConfiguredExecutableObjectsWithConditions<Animation,Unit>>(
    id, defaultValue, required
) {
    override val serializer: AbstractObjectArgumentSerializer<ConfiguredExecutableObjectsWithConditions<Animation, Unit>?>
        get() = Serializer

    override fun load(section: ConfigurationSection): ConfiguredExecutableObjectsWithConditions<Animation, Unit>? {
        return Serializer.load(section, "")
    }

    object Serializer : AbstractObjectArgumentSerializer<ConfiguredExecutableObjectsWithConditions<Animation, Unit>?>() {
        override fun load(
            section: ConfigurationSection,
            id: String
        ): ConfiguredExecutableObjectsWithConditions<Animation, Unit>? {
            return loadActionsWithConditions(section)
        }

        fun loadActionsWithConditions(section: ConfigurationSection): ConfiguredExecutableObjectsWithConditions<Animation,Unit>? {
            val actions = ArrayList<ConfiguredExecutableObjectWithConditions<Animation,Unit>>()
            val actionSections = section.getSectionList("actions")

            for (actionSection in actionSections) {
                actions += loadActionWithCondition(actionSection) ?: continue
            }
            val conditions = ArrayList<ConfiguredRequirementWithFailActions<Animation,Unit>>()
            for (conditionSection in section.getSectionList("conditions")) {
                conditions += loadConditionWithFailActions(conditionSection) ?: continue
            }

            if (actions.isEmpty() && conditions.isEmpty()) return null

            val failActions = if (section.isConfigurationSection("fail") && conditions.isNotEmpty()) {
                loadActionsWithConditions(section.getConfigurationSection("fail")!!)
            } else null

            return ConfiguredExecutableObjectsWithConditions(actions, conditions, failActions)
        }

        fun loadActionWithCondition(section: ConfigurationSection): ConfiguredExecutableObjectWithConditions<Animation, Unit>? {
            val action = ActionSerializer.fromSection<Animation>(section) ?: return null
            val conditions = ArrayList<ConfiguredRequirementWithFailActions<Animation,Unit>>()
            for (configurationSection in section.getSectionList("conditions")) {
                conditions += loadConditionWithFailActions(configurationSection) ?: continue
            }
            val failActions = if (section.isConfigurationSection("fail") && conditions.isNotEmpty()) {
                loadActionsWithConditions(section.getConfigurationSection("fail")!!)
            } else null
            return ConfiguredExecutableObjectWithConditions(action, conditions, failActions)
        }

        fun loadConditionWithFailActions(section: ConfigurationSection): ConfiguredRequirementWithFailActions<Animation, Unit>? {
            val condition = RequirementSerializer.fromSection<Animation>(section) ?: return null
            val failActions = if (section.isConfigurationSection("fail")) {
                loadActionsWithConditions(section.getConfigurationSection("fail")!!)
            } else null
            return ConfiguredRequirementWithFailActions(condition, failActions)
        }

    }
}