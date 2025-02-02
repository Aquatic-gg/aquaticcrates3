package gg.aquatic.aquaticcrates.api.util

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationActions
import gg.aquatic.waves.registry.serializer.ActionSerializer
import gg.aquatic.waves.registry.serializer.RequirementSerializer
import gg.aquatic.waves.util.argument.AbstractObjectArgumentSerializer
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.collection.checkRequirements
import gg.aquatic.waves.util.getSectionList
import gg.aquatic.waves.util.requirement.ConfiguredRequirement
import org.bukkit.configuration.ConfigurationSection

class ConditionalActionsArgument(
    id: String, defaultValue: ConditionalAnimationActions?,
    required: Boolean
) : AquaticObjectArgument<ConditionalActionsArgument.ConditionalAnimationActions>(
    id, defaultValue, required
) {
    override val serializer: AbstractObjectArgumentSerializer<ConditionalAnimationActions?>
        get() = Serializer

    override fun load(section: ConfigurationSection): ConditionalAnimationActions {
        return Serializer.load(section, "")
    }

    object Serializer :
        AbstractObjectArgumentSerializer<ConditionalAnimationActions?>() {
        override fun load(
            section: ConfigurationSection,
            id: String
        ): ConditionalAnimationActions {
            val actions = ActionSerializer.fromSections<Animation>(section.getSectionList("actions"))
            val playerBoundActions =
                ActionSerializer.fromSections<PlayerBoundAnimation>(section.getSectionList("actions"))

            val conditions = RequirementSerializer.fromSections<Animation>(section.getSectionList("conditions"))
            val playerBoundConditions =
                RequirementSerializer.fromSections<PlayerBoundAnimation>(section.getSectionList("conditions"))

            val failActions = ActionSerializer.fromSections<Animation>(section.getSectionList("fail"))
            val failPlayerBoundActions =
                ActionSerializer.fromSections<PlayerBoundAnimation>(section.getSectionList("fail"))

            return ConditionalAnimationActions(
                CrateAnimationActions(actions.toMutableList(), playerBoundActions.toMutableList()),
                conditions,
                playerBoundConditions,
                CrateAnimationActions(failActions.toMutableList(), failPlayerBoundActions.toMutableList())
            )
        }
    }

    class ConditionalAnimationActions(
        val actions: CrateAnimationActions,
        val conditions: List<ConfiguredRequirement<Animation>>,
        val playerBoundConditions: List<ConfiguredRequirement<PlayerBoundAnimation>>,
        val failActions: CrateAnimationActions,
    ) {

        fun tryExecute(animation: Animation) {
            var isMet = true
            if (animation is PlayerBoundAnimation) {
                if (!playerBoundConditions.checkRequirements(animation)) isMet = false
            }
            if (isMet && !conditions.checkRequirements(animation)) isMet = false

            if (isMet) {
                actions.execute(animation)
            } else {
                failActions.execute(animation)
            }
        }

    }
}