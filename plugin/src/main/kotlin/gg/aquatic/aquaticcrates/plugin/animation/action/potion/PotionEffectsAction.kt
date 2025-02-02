package gg.aquatic.aquaticcrates.plugin.animation.action.potion

import gg.aquatic.aquaticcrates.api.animation.PlayerBoundAnimation
import gg.aquatic.waves.util.argument.AbstractObjectArgumentSerializer
import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.ObjectArguments
import gg.aquatic.waves.util.generic.Action
import gg.aquatic.waves.util.getSectionList
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class PotionEffectsAction : Action<PlayerBoundAnimation> {
    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PotionsArgument("potions", mapOf(), true)
    )

    override fun execute(
        binder: PlayerBoundAnimation,
        args: ObjectArguments,
        textUpdater: (PlayerBoundAnimation, String) -> String
    ) {
        val potions = args.typed<Map<PotionEffectType, Pair<Int, Int>>>("potions") ?: mapOf()
        for ((type, pair) in potions) {
            val (duration, amplifier) = pair
            binder.player.addPotionEffect(PotionEffect(type, duration, amplifier, false, false, false))
            //binder.player.addPotionEffect(type, duration, amplifier)
        }
    }

    class PotionsArgument(
        id: String,
        defaultValue: Map<PotionEffectType, Pair<Int, Int>>?, required: Boolean
    ) : AquaticObjectArgument<Map<PotionEffectType, Pair<Int, Int>>>(id, defaultValue, required) {
        override val serializer: AbstractObjectArgumentSerializer<Map<PotionEffectType, Pair<Int, Int>>?> = Companion

        override fun load(section: ConfigurationSection): Map<PotionEffectType, Pair<Int, Int>>? {
            return serializer.load(section, id)
        }

        companion object : AbstractObjectArgumentSerializer<Map<PotionEffectType, Pair<Int, Int>>?>() {
            override fun load(section: ConfigurationSection, id: String): Map<PotionEffectType, Pair<Int, Int>>? {
                val map = mutableMapOf<PotionEffectType, Pair<Int, Int>>()
                for (configurationSection in section.getSectionList(id)) {
                    val type =
                        PotionEffectType.getByName(configurationSection.getString("potion") ?: continue) ?: continue
                    val duration = configurationSection.getInt("duration")
                    val amplifier = configurationSection.getInt("amplifier")
                    map += type to (duration to amplifier)
                }
                return map
            }

        }

    }

}