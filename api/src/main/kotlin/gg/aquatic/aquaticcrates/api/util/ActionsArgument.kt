package gg.aquatic.aquaticcrates.api.util

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimationActions
import gg.aquatic.waves.registry.serializer.ActionSerializer
import gg.aquatic.waves.util.argument.AbstractObjectArgumentSerializer
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.getSectionList
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

class ActionsArgument(id: String,
                      defaultValue: CrateAnimationActions?, required: Boolean
) : AquaticObjectArgument<CrateAnimationActions>(id, defaultValue, required) {
    override val serializer: AbstractObjectArgumentSerializer<CrateAnimationActions?>
        get() = Serializer

    override fun load(section: ConfigurationSection): CrateAnimationActions? {
        return Serializer.load(section, id)
    }

    object Serializer: AbstractObjectArgumentSerializer<CrateAnimationActions?>() {
        override fun load(
            section: ConfigurationSection,
            id: String
        ): CrateAnimationActions {
            val actions = ActionSerializer.fromSections<Animation>(section.getSectionList(id))
            val playerBoundActions = ActionSerializer.fromSections<PlayerBoundAnimation>(section.getSectionList(id))

            return CrateAnimationActions(
                actions.toMutableList(),
                playerBoundActions.toMutableList(),
            )
        }

    }
}