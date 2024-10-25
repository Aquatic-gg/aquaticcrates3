package gg.aquatic.aquaticcrates.plugin.animation.prop

import gg.aquatic.aquaticcrates.api.animation.Animation
import gg.aquatic.aquaticcrates.api.animation.prop.AnimationProp
import gg.aquatic.aquaticseries.lib.AquaticSeriesLib
import gg.aquatic.aquaticseries.lib.adapt.AquaticBossBar
import gg.aquatic.aquaticseries.lib.util.toAquatic
import org.bukkit.entity.Player

class BossbarAnimationProp(
    override val animation: Animation,
    var text: String,
    var color: AquaticBossBar.Color,
    var style: AquaticBossBar.Style,
    var progress: Double,
    var textUpdater: (Player, String) -> String = { _, str -> str }
) : AnimationProp() {

    val bossBar = AquaticSeriesLib.INSTANCE.adapter.bossBarAdapter.create(
        textUpdater(animation.player, text).toAquatic(),
        color,
        style,
        progress
        )

    init {
        bossBar.addPlayer(animation.player)
    }

    override fun tick() {
        bossBar.text = textUpdater(animation.player, text).toAquatic()
    }

    override fun onAnimationEnd() {
        bossBar.removePlayer(animation.player)
    }
}