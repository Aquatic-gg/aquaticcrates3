package gg.aquatic.aquaticcrates.plugin.animation.idle

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.crate.CrateAnimation
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.waves.util.audience.AquaticAudience
import org.bukkit.Location

class IdleAnimationImpl(
    override val baseLocation: Location,
    override val audience: AquaticAudience,
) : Animation() {
    override val props: MutableMap<String, AnimationProp>
        get() = TODO("Not yet implemented")

    override fun tick() {
        TODO("Not yet implemented")
    }

    override fun updatePlaceholders(str: String): String {
        TODO("Not yet implemented")
    }
}