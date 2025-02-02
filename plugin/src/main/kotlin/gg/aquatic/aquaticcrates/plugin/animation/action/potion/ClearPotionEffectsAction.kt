package gg.aquatic.aquaticcrates.plugin.animation.action.potion

import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.waves.util.action.AbstractAction
import gg.aquatic.waves.util.argument.AbstractObjectArgumentSerializer
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.potion.PotionEffectType

class ClearPotionEffectsAction: AbstractAction<PlayerBoundAnimation>() {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PotionsArgument("potions", listOf(), true)
    )

    override fun execute(
        binder: PlayerBoundAnimation,
        args: ObjectArguments,
        textUpdater: (PlayerBoundAnimation, String) -> String
    ) {
        val potions = args.typed<List<PotionEffectType>>("potions") ?: listOf()
        for (type in potions) {
            binder.player.removePotionEffect(type)
        }
    }

    class PotionsArgument(
        id: String,
        defaultValue: List<PotionEffectType>?, required: Boolean
    ) : AquaticObjectArgument<List<PotionEffectType>>(id, defaultValue, required) {
        override val serializer: AbstractObjectArgumentSerializer<List<PotionEffectType>?> = Companion

        override fun load(section: ConfigurationSection): List<PotionEffectType>? {
            return serializer.load(section, id)
        }

        companion object : AbstractObjectArgumentSerializer<List<PotionEffectType>?>() {
            override fun load(section: ConfigurationSection, id: String): List<PotionEffectType> {
                val list = mutableListOf<PotionEffectType>()
                for (s in section.getStringList(id)) {
                    list += PotionEffectType.getByName(s) ?: continue
                }
                return list
            }

        }

    }
}